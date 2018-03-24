# hexagram30/graphdb

*A graph database, built on Redis, for use by hexagram30 projects*

[![][logo]][logo-large]


## Usage

Standalone:

```
$ lein start-redis
```

For use as part of a component-based system, see
[hxgm30.mush.components.database][comp-graphdb].


## Resources

* http://redisgraph.io/commands/
* https://s3.amazonaws.com/artifacts.opencypher.org/openCypher9.pdf
* https://github.com/ptaoussanis/carmine
* https://github.com/ptaoussanis/carmine/blob/master/src/taoensso/carmine.clj
* http://download.redis.io/redis-stable/redis.conf

### Visualisation

* http://graphalchemist.github.io/Alchemy/#/docs


## License

Copyright © 2018, Hexagram30

Apache License, Version 2.0


<!-- Named page links below: /-->

[logo]: https://raw.githubusercontent.com/hexagram30/resources/master/branding/logo/h30-logo-1-long-with-text-x688.png
[logo-large]: https://raw.githubusercontent.com/hexagram30/resources/master/branding/logo/h30-logo-1-long-with-text-x3440.png
[comp-graphdb]: https://github.com/hexagram30/hexagramMUSH/blob/master/src/hexagram30/mush/components/database.clj
