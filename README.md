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
The usage of CLI application is described in [Quickstart](https://github.com/aivanovski/pico-automator#quickstart) section.

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

## API overview
The api namespace is `picoautomator.core`.

### Start the test
Function `start-flow` starts the test:
```clojure
(start-flow
  "Test Flow"
  (fn [automator]
    (-> automator
        (launch "org.wikipedia"))))
```

### Launch application
The application could be launched with `launch` function:
```clojure
(-> automator
    (launch "org.wikipedia")) ;; specify your application id
```

### Indentify views
Functions that interact with views (for example `tap-on`, `assert-visible`, `input-text` and etc.) require the view paramaters to be specified as follows.
By view id:
```clojure
{:id "viewId"} ;; corresponds to R.id.viewId in android application
```
By exact text:
```clojure
{:text "Search Wikipedia"}
```
By content description:
```clojure
{:content-desc "search-wikipedia"}
```
By patricular text matching:
```clojure
{:contains-text "Wikipedia"}
```

### Assertions
To assert whether an element is visbile or not visible following function could be used:
```clojure
(-> automator
    (assert-visible {:id "viewId"})
    (assert-not-visible {:id "viewId"}))
```

### Clicks
In order to click/tap on view:
```clojure
(-> automator
    (tap-on {:id "viewId"})
    (long-tap-on {:id "viewId"}))
```

### Input text
```clojure
(-> automator
    ;; Inputs text regardless of whether any text field is currently focused or not
    (input-text "Text")

    ;; Taps on view specified by view-parameters and then inputs text
    (input-text "Text" {:id "inputField"}))
```

### Controlling test lifecycle
In order to finish test successfully or with a failure depend on some extenal condition, functions `complete` and `fail` could be used:
```clojure
(-> automator
    ;; Finishes test successfully and prints the message
    (complete "The test is finished successfully")

    ;; Finishes test with an error and prints the message
    (fail "The test is failed"))
```

### Other functions
```clojure
(-> automator
    ;; Checks if view is visible and returns true of false
    (visible? {:id "viewId"})

    ;; Stops test execution for 5 seconds
    (sleep {:seconds 5})

    ;; Wait until view became visible on the screen for 15 seconds and checks every second
    (wait-until {:text "viewId"} {:seconds 15} {:seconds 1})

    ;; Returns object that represents view tree on the screen
    (ui-tree))
```

## Examples
More examples could be found [here](https://github.com/aivanovski/pico-automator/tree/main/samples)
