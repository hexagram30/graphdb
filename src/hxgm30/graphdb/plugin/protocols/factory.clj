(defprotocol DBFactoryAPI
  (connect [this] [this opts])
  (dbs [this])
  (destroy [this])
  (drop [this db]))
