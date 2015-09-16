(ns follow-git.core
  (:gen-class))
(require '[clojure.set :as s])
(require '[tentacles.orgs])
(require '[tentacles.users])

(declare follow-unfollowed-users)

(def auth (atom {}))
(def per-page 100)

(defn -main
  "Takes in a user password combo with a org to follow all the users in the org that
  are not currently being followed"
  [& [user password org]]
  (swap! auth assoc :auth (str user ":" password))
  (follow-unfollowed-users org))

(defn- entries-for-page [git-function search-term page] (git-function search-term (conj {:page page :per-page per-page} @auth)))

(defn- all-entries [git-function search-term]
  (reduce (fn [coll curr]
              (let [entries (entries-for-page git-function search-term curr)]
              (if (map? entries) (throw (Exception. (str "Github Error: " entries))))
              (if (= (count entries) 0)
                  (reduced coll)
                  (flatten (conj coll entries)))))
              []
              (iterate inc 1)))

(defn members-of-org [org]
  (println "Finding all members")
  (all-entries tentacles.orgs/members org))

(defn- follow [user]
  (println "Following user" user)
  (future (tentacles.users/follow user @auth)))

(defn- following? [user]
  (future (tentacles.users/follow user @auth)))

(defn follow-unfollowed-user [user]
  (future (if (not (following? user))
            (do (follow user) (println "following: " user))
            false)))

(defn follow-unfollowed-users [org]
    (let [org-members (members-of-org org)]
      (println "Newly followed user count: "
               (doall (->> org-members
                  (map :login)
                  (map follow-unfollowed-user)
                  (map deref)
                  (filter true?))))
      (println "DONE")))
