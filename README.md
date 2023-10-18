[![](https://jitpack.io/v/aivanovski/pico-automator.svg)](https://jitpack.io/#aivanovski/pico-automator)

## pico-automator
Small UI automation library for Android built on top of [ADB](https://developer.android.com/tools/adb) calls. It provides API to write readable and dynamic tests on Clojure that could be run inside REPL or from command line.

## Demo
![demo](https://github.com/aivanovski/pico-automator/blob/main/images/demo.gif)

## Motivation
*pico-automator* is inspired by [maestro](https://github.com/mobile-dev-inc/maestro) and allows to write UI tests on Clojure. The choice of Clojure allows to use a lightweight IDE (VS Code, Emacs, Vim and etc.) and REPL when writing tests.

## Quickstart
1. Create file `example.clj` with the code below:
```Clojure
(start-flow
  "Sample Flow"
  (fn [automator]
    (-> automator
        (launch "org.wikipedia")
        (tap-on {:text "Search Wikipedia"})
        (assert-visible {:text "Recent searches:"})
        (input-text "Monad")
        (assert-visible {:text "Monad (functional programming)"}))))
```
2. Download `pico-automator.jar` from [Release page](https://github.com/aivanovski/pico-automator/releases) and run the test (in order to work, *pico-automator* requires Java 11 and ADB binary accessible from the PATH variable)
```
java -jar pico-automator.jar example.clj
```

## Usage
There are 2 ways to run tests with *pico-automator*:
- With standalone CLI application `pico-automator.jar`
- With Clojure REPL

### Run tests with CLI application
The usage of CLI application is described in [Quickstart](https://github.com/aivanovski/pico-automator/tree/feature/update-readme#quickstart) section.

### Run tests with Clojure REPL
The Functional API for *pico-automator* is provided by [pico-automator-clojure](https://github.com/aivanovski/pico-automator-clojure) project, that can be used as a dependency inside `deps.edn` file. [pico-automator-clojure](https://github.com/aivanovski/pico-automator-clojure) is available in JitPack maven repository [here](https://jitpack.io/#aivanovski/pico-automator-clojure).

1. Setup `deps.edn` file in the directory:
```Clojure
{:paths ["."]

 :mvn/repos
 {"jitpack" {:url "https://jitpack.io/"}}

 :deps
 {com.github.aivanovski/pico-automator-clojure {:mvn/version "0.0.11"}}

 :aliases
 {:repl-server {:extra-deps {nrepl/nrepl       {:mvn/version "1.0.0"}
                             cider/cider-nrepl {:mvn/version "0.37.0"}}
                :main-opts  ["--main"       "nrepl.cmdline"
                             "--middleware" "[cider.nrepl/cider-middleware]"]}}}
```

2. Start the REPL server:
```
clojure -M:repl-server
```

3. Create file with the test:
```Clojure
(ns wikipedia-search
  (:require [picoautomator.core :refer :all]))

(defn -main
  [& args]

  (start-flow
    "Search Wikipedia"
    (fn [automator]
      (-> automator
          (launch "org.wikipedia")
          ... ; Implement your test steps here
          ))))
```

4. Connect to the REPL and run it.

The example could be found [here](https://github.com/aivanovski/pico-automator/tree/main/samples/sample-clojure)

## API
The api namespace is `picoautomator.core`.

### Start the test
```
(start-flow name flow)
```

## Examples
More examples could be found [here](https://github.com/aivanovski/pico-automator/tree/main/samples)
