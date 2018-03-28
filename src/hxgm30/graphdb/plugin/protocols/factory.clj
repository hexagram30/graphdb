(defprotocol DBFactoryAPI
  (connect [this] [this opts])
  (destroy [this]))
