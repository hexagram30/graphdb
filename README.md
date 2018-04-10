# hexagram30/graphdb

*A graph database, built on Redis, for use by hexagram30 projects*

[![][logo]][logo-large]


## Usage

This project utilizes a plugin scheme to limit the number of dependencies
brought in for any given supported backend. This data is stored in a
project profile for each supported backend. For profiles that require an
external database, aliases for staring and stopping the databases are also
provided in the profile.

To start the database:
```
$ lein with-profile +redis-plugin start-db
```

To start the REPL:
```
$ lein with-profile +redis-plugin repl
```

Then call `(startup)` and `(shutdown)` to manage the associated components,
connecting to the database indicated in the plugin.

To connect to Redis via `redis-cli` inside the running container:
```
$ ./resources/scripts/redis-cli
```

When done, to stop the database:
```
$ lein with-profile +redis-plugin stop-db
```

For use as part of a component-based system, see any of the `component.clj`
files nested under the `plugins` directory.


## Resources

* https://github.com/lambdazen/bitsy
* http://janusgraph.org
* https://orientdb.com/docs/last/index.html
* http://redisgraph.io/commands/
* https://redis.io/commands/


### Visualisation

* http://graphalchemist.github.io/Alchemy/#/docs


## License

Copyright © 2018, Hexagram30

Apache License, Version 2.0


<!-- Named page links below: /-->

[logo]: https://raw.githubusercontent.com/hexagram30/resources/master/branding/logo/h30-logo-1-long-with-text-x688.png
[logo-large]: https://raw.githubusercontent.com/hexagram30/resources/master/branding/logo/h30-logo-1-long-with-text-x3440.png
[comp-graphdb]: https://github.com/hexagram30/hexagramMUSH/blob/master/src/hexagram30/mush/components/database.clj
