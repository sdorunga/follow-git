(ns follow-git.core
  (:gen-class))
(require '[clojure.set :as s])
(require '[tentacles.orgs])
(require '[tentacles.users])

(declare follow-unfollowed-users)

(def auth (atom {}))

(defn -main
  "Takes in a user password combo with a repo to follow all the users in the repo that
  are not currently being followed"
  [& [user password repo]]
  (swap! auth assoc :auth (str user ":" password))
  (println (:auth @auth) "-" repo)
  (follow-unfollowed-users user repo))

(defn all-entries
  [git-function search-term]
  (letfn [(entries-for-page [page] (git-function search-term (conj {:page page} @auth)))]
    (reduce  (fn [coll curr]
               (let [entries (entries-for-page curr)]
                 (if (or (= (count entries) 0)
                         (map? entries))
                  (reduced coll)
                  (flatten (conj coll entries)))))
             []
             (iterate inc 1))))

(defn users-following [user]
  (println "Finding all followers")
  (future (all-entries tentacles.users/following user)))

(defn members-of-org [org]
  (println "finding all members")
  (future (all-entries tentacles.orgs/members org)))

(defn follow-unfollowed-users [user repo]
  (letfn [(set-of-logins [users] (into #{} (map :login users)))
          (follow [user] (println "Following user" user) (future (tentacles.users/follow user @auth)))]
    (let [followed (users-following user)
          org-members (members-of-org repo)
          unfollowed-users (s/difference (set-of-logins @org-members) (set-of-logins @followed))]
      (doall (->> unfollowed-users
                  (map follow)
                  (map deref)))
      (println "Newly followed users: " (count unfollowed-users))
      (println "DONE"))))
