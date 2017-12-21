# Contributing to cemu_UI
The following is a set of guidelines for contributing to cemu_UI.

## Translating
If you don't want to contibute any code you can support this project by translating it! The translation files are stored in "src/main/resources/locals".

# Contributing code
If you want to contibute code please read the java code styleguide.

## Java code sytleguide
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
