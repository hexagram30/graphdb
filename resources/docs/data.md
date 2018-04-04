# Data

The hexagram30 graphdb service needs to support all sorts of different data
sets for HexagramMUSH. Static data a la attributes will likely include the
following:

* room data
* building data
* city data
* region data
* world data
* user registration data
* character data
* player data (current playing character for a user)
* NPC data

Furthermore, there will be data that changes on a second-by-second basis,
representing the state of a given thing:
* world state data
* region state data
* city state data
* building state
* room state data
* player state data
* NPC state data
* item state data

In both cases, there's a lot to plan for and track, We'll get to that
eventually, but for now we'll keep the focus small and manageable: room data,
particularly around room construction and basic movement between rooms.

## Example: Room

### Creating

TBD

### Modifying

TBD

### Querying Attributes

TBD

### Querying Relationships

Questions we want to answer:

* what room is a player in?
* what exits does a room have?
* where do those exits lead?
* who is in a given room?
* what items are in a given room?

Taking these in turn:

* what room is a player in?

Steps:

  - player name -> player id
  - lookup player state, extract location attribute (node id)
  - look up node (room) data, extract room name

* what exits does a room have?

and

* where do those exits lead?

Steps:

  - get room id
  - get relations for current room
  - look up edge data for each exit
  - look up room data for each destination node

* who is in a given room?

Steps:

  - get room id
  - ???
    . query room state? (maybe store occupants in node/room id SET)
    . store

* what items are in a given room?
