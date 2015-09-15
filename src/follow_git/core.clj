(ns follow-git.core
  (:require [clojure.set :as s]
            [tentacles.orgs]
            [tentacles.users])
  (:gen-class))

(declare follow-unfollowed-users)

(def auth (atom {}))

(defn -main
  "Takes in a user password combo with a repo to follow all the users in the repo that
  are not currently being followed"
  [& [user password repo]]
  (swap! auth assoc :auth (str user ":" password))
  (follow-unfollowed-users user repo))

(defn- entries-for-page [git-function search-term page] (git-function search-term (conj {:page page} @auth)))

(defn all-entries [git-function search-term]
  (future
    (reduce (fn [coll curr]
              (let [entries (entries-for-page git-function search-term curr)]
                (if (map? entries) (throw (Exception. (str "Github Error: " entries))))
                (if (= (count entries) 0)
                  (reduced coll)
                  (flatten (conj coll entries)))))
             []
             (iterate inc 1))))

(defn users-following [user]
  (println "Finding all followers")
  (all-entries tentacles.users/following user))

(defn members-of-org [org]
  (println "Finding all members")
  (all-entries tentacles.orgs/members org))

(defn- set-of-logins [users]
  (into #{}
        (map :login users)))

(defn- follow [user]
  (println "Following user" user)
  (future (tentacles.users/follow user @auth)))

(defn follow-unfollowed-users [user repo]
    (let [followed (users-following user)
          org-members (members-of-org repo)
          unfollowed-users (s/difference (set-of-logins @org-members) (set-of-logins @followed))]
      (doall (->> unfollowed-users
                  (map follow)
                  (map deref)))
      (println "Newly followed users: " (count unfollowed-users))
      (println "DONE")))
