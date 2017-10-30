# Contributing to cemu_UI
The following is a set of guidelines for contributing to cemu_UI.

## Java Sytleguide
If your willing to contribute to cemu_UI please us the following example as guide and rules to design your code.
* Use names for methods and variables that clarify their purpose. (This will help a lot to understand the code)
* Use as many spaces as necessary to make the code clear, but as little as possible.
* Sort all variables according to their type.
* For all (debugging) console outputs, use the log4j based logger. (syso/syse is okay for testing)

 ```java
  // Use this as a sample Class:
  public ClassName () {
    
    void MethodName () {
       double gameID; // Title-ID used on the Wii U
       
       if (gameID == null) {
          // Do something
       }
       
       // more code ...
    }
    
  }

  ```
