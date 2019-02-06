(ns memory-puzzle.game)

(def card-types [:a :b :c :d :e :g])

(defn- gen-cards [width height card-types]
  (into {} (map (fn [[k v] t] [k (assoc v :type t)])
                (for [x (range width)
                      y (range height)]
                  [[x y] {:flipped false}])
                (apply concat (repeatedly 2 (partial shuffle card-types))))))

(defn- eq-cards? [c1 c2]
  (= (:type c1) (:type c2)))

(defn- selected-is-equals? [state]
  (let [{:keys [selected cards]} state
        [s1 s2] selected]
    (if (seq selected)
      (eq-cards? (get cards s1) (get cards s2))
      false)))

(defn initial-state [width height card-types]
  {:cards (gen-cards width height card-types)
   :selected []
   :goal (* width height)})

(defn pick-card [state cell]
  (let [selected (:selected state)
        sc (count selected)]
    (cond
      (get-in state [:cards cell :flipped]) state
      (< sc 2) (-> state
                   (update :selected conj cell)
                   (assoc-in [:cards cell :flipped] true))
      :else state)))

(defn finish? [state]
  (= (:goal state) 0))

(defn make-turn [state]
  (if (selected-is-equals? state)
    (-> state
        (assoc :selected [])
        (update :goal #(- % 2)))
    (let [[s1 s2] (:selected state)]
      (-> state
          (assoc-in [:cards s1 :flipped] false)
          (assoc-in [:cards s2 :flipped] false)
          (assoc :selected [])))))
