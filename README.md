# slack-clojure-repl

This project provides slack with a clojure repl. In order to do the minimum
amount of work to get the repl going, I leveraged the
[Try Clojure](http://tryclj.com) website.

## Prerequisites

You will need [Leiningen][1] 1.7.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

```
lein ring server-headless
```

which will start a server on port 3000.

## Deploying

Deploying to heroku is taken care of. If you haven't deployed a clojure
application with Heroku before, see the
[getting started](https://devcenter.heroku.com/articles/getting-started-with-clojure#introduction)
page.

```
heroku config:set SLACK_WEBHOOK='https://...'
```

## TODO

* Perhaps leverage other websites to easily create repl's for other languages

## License

Released under the Eclipse Public License.
