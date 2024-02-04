# Quick Dial
## _Building Ussd application the declarative way_ 

>Design necessity and philosophy:  **_Anything that can be built, can be built
>declaratively!_**

- [Dependency](#dependency)
- [Ussd Architecture](#ussd-architecture)
- [Autoconfiguration](#auto-configuration)
- [QuickDialPayload](#payload)
- [Ussd mapping](#ussd-mapping)
- [UssdExecution](#ussd-execution)
- [Interceptors](#interceptors)
- [Messaging](#messaging)
- [Events](#events)
- [ApplicationStore](#application-store)
- [Statistics API](#statistics-api)
- [Best Practices](#best-practices)
- [Examples with Africa-Is-Talking](#example)


## Dependency

## Ussd Architecture
<p align="justify">
In every Ussd application, there is primary three (4) parties involved. These individual component parties completes the entire lifecycle for every USSD request made by the user via their mobile devices.
</p>

The diagram below shows the summary and flow of connection and call between the individual components.




## Autoconfiguration
<p align="justify">
  Once the starter library is added to the developer's Springboot application, the necessary credentials of the Ussd integration should be declared in the application.yml or application.properties file.
  Below declares the base code of the ussd action (this base code will be generated when the developer/company is fully onboarded by the UssdProvider of choice. The Ussd provider will onboarded and provide the integrated ussd code afterwards.

```properties
    ussd.properties.config.base-ussd-code=*123#
    ussd.properties.config.go-back-option=0
    ussd.properties.config.go-forward-option=00
```
With the above configurations, <strong> quickdial </strong> will automatically configure all that it needs to start. Also from the above, the declarations made in the properties file is obvious for what they intend to do.
For example, whenever a ussd user enters <strong> 0 </strong> as an input, the framework redirects the user to the previous ussd page of the application automatically. The user will also be redirected <strong> <i> forward </i></strong> when he enters <strong> 00 </strong> as an input. The whole of the navigation algorithm is handled by <strong>quickdial</strong> and thus, the developer does not have to do any other configuration to achieve this.
</p>


## QuickDialPayload
<p align="justify">
The entry point between <span>quickdial</span> and a typical springboot application is the QuickDialPayload. The QuickDialPayload represents all that is needed to be passed to the <span style="font-weight: bold">quickdial</span> library. The library then will then manage all necessary complexities for Ussd session management, automatic redirection, continuation, termination, navigation and option menu input validation.
Typically, the QuickDialPayload will be created from the incoming input of the Ussd Provider or vendor and constructed in the service layer of the springboot application. The <a>QuickDialUssdExecutor</a> interface in the library accepts the QuickDialPayload as the only input and returns a generic ussd execution result. The generic return type of the QuickDialUssdExecutor is of the same type as the return type defined by the developer in the Ussd menu handler (to be discussed below).
</p>

<p align="justify">
<span style="font-weight: bold">Note</span>: Due to the fact that diverse Ussd Provider of choice have different payload they supplier to the developer's controller during integration, it is the responsibility of the developer to adjust the incoming Ussd Provider payload to the QuickDialPayload for use in this library. This of course, is the only work that the developer needs to do to get the library up and running.
</p>

Below outlines each fields of the QuickDialPayload.

| Fields        | Description                                                                                                                                                                                                                                                                                                                                                                                                     | Example Value                                               |
|---------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------|
 | **sessionId** | This is the unique sessionId associated with the user's current session. It is usually supplied from the Ussd Provider and should be passed down to the QuickDialPayload.                                                                                                                                                                                                                                       | 2268b0f0-7ff5-4782-bb85-368cb975b9ea                        |
  | **msisdn**    | This is the unique mobile number of the user. This is supplied by the Network Provider to the Ussd Provider who in turn supplies it to the developer's springboot controller.                                                                                                                                                                                                                                   | 07021324354                                                 |
  | **telco** | This represents the Network provider of the user as at the time of the Ussd call. Again it is supplied by the Network provider via the Ussd Provider to the developer's springboot application                                                                                                                                                                                                                  | MTN                                                         |
 | **originatingCode** | This represents the first Ussd code dialed by the user. Developer's can use this to determine if the user is dialing the **base code** or a **short code** associated with the Ussd integration.                                                                                                                                                                                                                | *123# (base code) and *123*1*2# (short code).               |
 | **prefix** | This is used basically for simple routing of the Ussd request. Enables clean separation of logic based on the nature of the customer or user making the request                                                                                                                                                                                                                                                 | NEW_CUSTOMER                                                |
 | **input** | This represent the current input of the user in the current ussd session                                                                                                                                                                                                                                                                                                                                        | 1                                                           |
 | **invocationType** | This represent the ussd invocation type either of **progressive** or **shortcode**. It is the responsibility of the developer to study their respective Ussd provider and decide when a request is a progressive request or a shortcode request. In the **quickdial** library, the UssdInvocationType is an enum. If this is defined by the developer, the UssdInvocationType.PROGRESSIVE is used as a default. | UssdInvocationType.PROGRESSIVE                              |
 | **shortCodeString** | A boolean that specifies if the incoming dialed code of the user is a **shortcodestring**. Again, it is the responsiblity of the developer to figure out the behaviour of the Ussd Provider to ascertain when a shortcodestring is passed. The default value is **false**                                                                                                                                       | true                                                        |
 | **sessionStarting** | A boolean that specifies if the incoming dialed code of the user is a **starting session** . Again, it is the responsibility of the developer to know when the Ussd provider is starting session.                                                                                                                                                                                                               | **true**  |

## Ussd mapping
<p align="justify">
The philosophy of the quickdial library is very similar to the philosophy of springboot request handling. Every possible ussd operation initiated by user actions are mapped into ussd menu handlers. In the most basic form, a menu can be represented as a class while submenus will simply be methods in the class.
Annotations are used to specify the menu codes as well as the submenu codes of the ussd action. The following snapshot shows a simple menu with 3 submenu.
<div style="text-align: center"> <img src="https://aq.suretrade.pro/man.jpg" width="100" height="100" /> </div>

From the above, the menu is of number 1 (i.e menu item 1) and has three submenus.
Now suppose that we intend to map this relationship, we can create a class called **TestMenuHandler** for example, and then create the three(3) submenus in the methods.
</p>

```java
// imports

import com.quantumforge.quickdial.annotation.UssdParam;
import com.quantumforge.quickdial.annotation.UssdSubMenuHandler;
import com.quantumforge.quickdial.context.UserUssdContext;
import com.quantumforge.quickdial.session.SessionData;
import com.quantumforge.quickdial.session.UssdSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@UssdMenuHandler(menu = "1")
public class TestMenuHandler {

 private final UserService userService;

 @UssdSubMenuHandler    // => *123*1#
 public String showAccountOpeningMenus(UserUssdContext userUssdContext, SessionData sessionData, UssdSession ussdSession) {

     return "1. Open account with BVN \n2. Open account without BVN";
 }


 @UssdSubMenuHandler(submenu = "1")   // => *123*1*1#
 public String enterBVNForAccountOpeningPage(UserUssdContext userUssdContext, UssdSession ussdSession) {
     return "Enter your BVN";
 }

 @UssdSubMenuHandler(submenu = "*1*{bvnEntered}#")  // => *123*1*1*1123456789#
 public String showBVNDetailsPage(@UssdParam("bvnEntered") String bvn, UserUssdContext userUssdContext, UssdSession ussdSession, SessionData sessionData) {
     // log the bvn entered by the user. The @UssdParam annotation binds the bvn entered by the user the bvn variable.
     log.info("Customer BVN entered -------------------------{}", bvn);
     // Store the bvn entered by the user to the injected SessionData store
     sessionData.keepAttribute("userBvn", bvn);
     return "Name attached to BVN details is: John Doe.\n1. Continue \n2. Cancel";
 }

  @UssdSubMenuHandler(submenu = "*1*{bvnEntered}*1#")  // *123*1*1*1123456789*1#
  public String showSuccessPage(@UssdParam("bvnEntered") String bvn, UserUssdContext userUssdContext, UssdSession ussdSession, SessionData sessionData) {
     String mobileNumber = userUssdContext.getMsisdn();  // User mobileNumber sent from the UssdProvider via the network provider.
     boolean success = userService.createUserAndAccountWithBvn(bvn, mobileNumber);
     if(success){
         return "Congrats!. Account created successfully";
     }else{
         return "Oops! something went wrong";
     }
  }


  // Create handler methods to handle Submenu 2 (Opening Account without BVN) ...
  @UssdSubMenuHandler(submenu = "2")   // *123*1*2#
  public String enterFirstNamePage(UserUssdContext userUssdContext, UssdSession ussdSession) {
 
  }

}

```
Many more example code of ussd mapping will be illustrated further in this documentation. Dynamic examples will be provided in the section where this library is used to integrate with **Africa-Is-Talking** Ussd provider.



## UssdExecution
<p align="justify">
 The behaviour of every session for a user can be controlled by the developer. For every ussd session/interaction, there are basically three(3) that can happen.
</p>

- Continue session
- Redirect session
- End session

The following code snippets illustrates how the developer can control the flow of the ussd session for the user. We will rewrite the above sample codes but with the use of the UssdExecution static operational methods for ussd session control flow.

```java

import com.quantumforge.quickdial.annotation.UssdParam;
import com.quantumforge.quickdial.annotation.UssdSubMenuHandler;
import com.quantumforge.quickdial.context.UserUssdContext;
import com.quantumforge.quickdial.payload.UssdExecution;
import com.quantumforge.quickdial.session.SessionData;
import com.quantumforge.quickdial.session.UssdSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@UssdMenuHandler(menu = "1")
public class TestMenuHandler {

 private final UserService userService;

 @UssdSubMenuHandler    // => *123*1#
 public UssdExecution<String> showAccountOpeningMenus(UserUssdContext userUssdContext, SessionData sessionData, UssdSession ussdSession) {
    return UssdExecution.continues("1. Open account with BVN \n2. Open account without BVN");   // This continues the ussd session on showing this message or page.
 }


 @UssdSubMenuHandler(submenu = "1")   // => *123*1*1#
 public UssdExecution<String> enterBVNForAccountOpeningPage(UserUssdContext userUssdContext, UssdSession ussdSession) {
        return UssdExecution.continues("Enter your BVN"); 
 }

 @UssdSubMenuHandler(submenu = "*1*{bvnEntered}#")  // => *123*1*1*1123456789#
 public UssdExecution<String> showBVNDetailsPage(@UssdParam("bvnEntered") String bvn, UserUssdContext userUssdContext, UssdSession ussdSession, SessionData sessionData) {
    log.info("Customer BVN entered -------------------------{}", bvn);
    sessionData.keepAttribute("userBvn", bvn);
    return UssdExecution.continues("Name attached to BVN details is: John Doe.\n1. Continue \n2. Try again");
 }

 @UssdSubMenuHandler(submenu = "*1*{bvnEntered}*{continueOption}#")  // *123*1*1*1123456789*1#
 public UssdExecution<String> showSuccessPage(@UssdParam("bvnEntered") String bvn, @UssdParam("continueOption") String option, UserUssdContext userUssdContext, UssdSession ussdSession, SessionData sessionData) {
     // User entered 1 from above. Thus, user wants to continue
     if(option.equalsIgnoreCase("1")) { 
         String mobileNumber = userUssdContext.getMsisdn();  // User mobileNumber sent from the UssdProvider via the network provider.
         boolean success = userService.createUserAndAccountWithBvn(bvn, mobileNumber);
         if (success) {
             return UssdExecution.continues("Congrats!. Account created successfully");
         } else {
             return UssdExecution.end("Oops! something went wrong");
         }
     }
     
     // User entered 2. Thus, user claims that the BVN is not his and wants to try again. We will then redirect user to the page before the above page.
     else if(option.equalsIgnoreCase("2")){
         UssdExecution.redirect("this::enterBVNForAccountOpeningPage");  // method to be redirected to is in same class. Thus use 'this' for shorthand
         UssdExecution.redirect("TestMenuHandler::enterBVNForAccountOpeningPage"); // another way of redirecting with 'class::method' reference
     }
  }

}

```

## Interceptors
<p align="justify">
Interceptors are very important aspect of the quickdial library. They provide automatic interference to the smooth and smart behaviour of the ussd application.
</p>
<p align="justify">
There are 3 major interceptors currently supported.
</p>

- Backward navigation interceptor
- Forward navigation interceptor
- Option check interceptor

<p align="justify">
The Backward and the forward interceptors have been discussed in the <strong>AutoConfiguration</strong> section of this documentation.
</p>
<p align="justify">
The Option check interceptor is an interceptor that ensures that the <strong>input</strong> correctly entered by the user is among the <strong>menu options</strong> displayed by the previous page.
If the previous page is not a type that has menu options, this interceptor passes. Thus it makes menu validation easy for the developer by abstracting away the logic of menu options to user input validations. Developers can focus on the implementations based on different input of the user without bothering about the input validation.

Following below dictates how this feature can be autoconfigured
</p>

```properties
 ussd.properties.config.enable-menu-option-check=true
 ussd.redirect.enable-automatic-error-redirection-message=true 
 ussd.redirect.inputValidationErrorRedirectReference=class::method (the default is the first page)
 ussd.redirect.defaultInputValidationMessage=Invalid input. Please select correct option. [[${errorRetryAttemptLeft}]] [[${errorRetrySuffix}]] left.
```
Also, an option can be passed to the UssdSubMenuHandler annotation of the handler methods to instruct **quickdial** to skip menu option validation for the said handler.

```java

import com.quantumforge.quickdial.annotation.UssdSubMenuHandler;

public class TestMenuHandler {
    
    // Turn off menu option check
    @UssdSubMenuHandler(submenu = "1", relaxMenuOptionCheck = true)
    public UssdExecution<String> showMenus() {

    }
}
```

## Messaging
<p align="justify">
It is a common practice for ussd messages to be hardcoded in the ussd application. The <strong>quickdial</strong> library helps to provide sane abstraction of messages from the main ussd application logic.
</p>
<p align="justify">
The quickdial library provides two options for building messages sent to the ussd user. These options include
</p>

1. Class based message builders. This includes:
     - SimpleLineWriter
     - OptionLineWriter
     - CompositeLineWriter
   
The code snippets below illustrates how to use the OptionLineBuilder to create create dynamic messages with options in the same instance they are inserted.

```java

import com.quantumforge.quickdial.annotation.UssdSubMenuHandler;
import com.quantumforge.quickdial.messaging.template.instrumentation.OptionLineWriter;
import com.quantumforge.quickdial.payload.UssdExecution;


public class TestMenuHandler {

     @UssdSubMenuHandler(submenu = "1")    // => *123*1#
     public UssdExecution<String> showAccountOpeningMenus(UserUssdContext userUssdContext, SessionData sessionData, UssdSession ussdSession) {
          String message = OptionLineWriter.start()
                  .addLine("1", "Open account with BVN")
                  .addLine("2", "Open account without BVN")
                  .join();  // Build the final message by calling the 'join()' method.
          return UssdExecution.continues(message);
     }
}
```
.

2. **Xml Based message builder**

The above shows how the developer can build his/her message to the ussd user within the ussd logic. However, the problem of abstraction still remains a problem here.
It is clear that the message is not totally separated from the application menu handling logic.

The use of XML based message templates helps to fulfill this requirement. The following shows the steps to using the XML based messaging technique

- **Step 1**
  
    Configure the single source folders of all your messages. The folder will be scanned by the **quickdial** library at application startup and keep them in its message contexts and later make available for injection when needed.
    
    There are two ways to provide the source folder for all messages.
    
   1. Enable springboot autoconfiguration

    ```properties
        quick-dial.template-path=#your template path (default is 'quickdial') in /resources/quickdial
    ```
  2. Manually provide a bean of the **QuickDialMessageResource** in a ny discoverable Configuration class.

    ```java
        
        import com.quantumforge.quickdial.messaging.bean.QuickDialMessageResource;import org.springframework.context.annotation.Bean;import java.io.File;public class TestMenuHandler{
  
  
            @Bean
            public QuickDialMessageResource quickDialMessageResource(){
                return QuickDialMessageResource.builder()
                    .primaryResourceFolder(new File("/path/to/file"))
                    .name("optional custom name for the file")
                    .build();
            }     
        } 
    ```
    It should be noted that the developer can provide as much XML template files as possible. The xml files can also be nested within the source root folder configured by any of the above options. The **quickdial** library will **_recursively_** scan all messages in all files and store its in its context for use.

The following snapshot shows a sample of a developer's resources folder for the ussd application. Here the **quickdial** folder is the root source of the message xml files. This means ALL xml files will be scanned **_recursively_** for their internal messages.

![img_1.png](img_1.png)
.

Now, how do we get the **_qualified name_** of the document? How does the **quickdial** library knows which document contains which message?

The simple answer to the above question is that the **quickdial** library uses the qualified name of each document. The qualified name of each document is obtained by navigating recursively the relative path of the xml file starting from the root source. Then each relative path is concatenated one to another with a configurable joiner. By default, this joiner is an underscore ( **_** )

From the above snapshot, we can have the following qualified name for each xml message resource.

| File name | Relative path                                  | qualified name |
|-----------|------------------------------------------------|----------------|
 | quickdial.xml | / resources / quickdial / home / quickdial.xml | home_quickdial |
 | account.xml | / resources / quickdial / account.xml | account.xml |

.
- **Step 2**

Write messages in the xml files. The following depicts a typical xml message source and note its **_qualified name_**
```xml
<?xml version="1.0" encoding="UTF-8" xmlns:th="http://www.thymeleaf.org" lang="en" ?>
<ussd>
    <quickdial>
        <messages>

            <message id="charge">
                <line> Dear customer, a charge of N5.00 will be applied for this service. </line>
                <line/>
                <line option="1"> Continue </line>
                <line option="2"> Cancel </line>
                <line option="3"> [[${accountNumber}]] </line>
                <line option="4"> [[${customerId}]] </line>
                <fragment th:each="name, index:${preferences}">
                    <line option="${index.count}"> [[${name}]] </line>
                </fragment>
            </message>
        </messages>
    </quickdial>
</ussd>
```

The following rules must be applied when using the XML message template:

- No two message tags can have the same **messageId** in the file. The messageId must be a unique attribute of the message tag.
- There is no hard rule to the structure of the xml template. However, the **_all message tag must be within the messages tag_**
- If no messageId is supplied in the message tag attribute, **quickdial** will generate a random string id.

.

3. **Step 3**

Inject the document of choice in a handler class by virtue of its qualified name. The operation and functionality of the documents is held in a **UssdMessageDocumentResolver** interface.

The following illustrates the use of the UssdMessageDocumentResolver interface to read a message given a specified **messageId**

```java
    
    public class TestMenuHandler{

        @InjectDocument("home_quickdial")    // use the qualified name to inject the document to be used
        private UssdMessageDocumentResolver documentResolver;

        @UssdSubMenuHandler
        public String showStartPageOfCharges(UssdModel model){
            model.addObject("accountNumber", "2020202020");
            model.addObject("customerId", "123456");
            List<String> userPreferences = Arrays.asList("Account creation", "Customer Onboarding");
            model.addObject("preferences", userPreferences);
            return documentResolver.withModel(model).getResolvedMessageById("charge");  // Get a particular message by virtue of the messageId in the qualified document.
        }
    }
```

From the above, it is clear that there is now total separation between the ussd messages and the application logic. 

Also, the xml file supports all templating pattern provided by the developer choice of common template engines. **quickdial** supports two (2) template engines at the moment.

- Thymeleaf (default)
- Apache freemarker

The following configurations shows how to choose the preferred template engine for the developer.

```properties
    quick-dial.messages.template.preferredEngine =thymeleaf
```

Other configurations related to the messaging includes

```properties
    quick-dial.enableVerboseTemplateLogging=true                            // logs verbose information about each document at application startup
    quickdial.nestedFileSeparator=UNDER_SCORE (other value is DOT)          // specify the joiner or operator for the qualified name of the documents
    quickdial.optionToMessageSeparator=". " (default)                       // specifies the joiner between each option and content of a message line
```
The above shows that all templating pattern of the thymeleaf will be supported and the thymeleaf template context for data replacement will be read from the **UssdModel** auto-injectable in the UssdSubMenuHandler methods.

## Events
<p>
Throughout the lifecycle of the Ussd application, **quickdial** emits certain important events that the developer can hook up on to perform certain operation. The table below shows a summary of the events, the time of events publication and the event object available for use in the listener operation.
</p>

| Event | Description                                                                                                                       | Event object         | 
|-------|-----------------------------------------------------------------------------------------------------------------------------------|----------------------|
| UssdMappingExecutionContextInitializedEvent | Published when all sole and group ussd mappings have been initialized                                                             | List(UssdExecutable) |
| UssdMessageDocumentContainerInitializedEvent | Published when all message source files have been successfully scanned and all messages therein are stored in the message context | MessageDocuments |
| UssdUserSessionInitializedEvent | Published when a new session for a user is initialized successfully                                                               | UssdSession source, SessionInitData sessionInitData |
| UssdUserSessionPostDestroyedEvent | Publised just after a user session is destroyed.                                                                                  | UssdSession (with all fields as NULL) |
| UssdUserSessionPreDestroyedEvent | Publised just before a user session is destroyed | UssdSession |
| UssdUserSessionUpdatedEvent | Publised when a user session is updated as the user continues through the session | UssdSession source, SessionInitData initData |



## ApplicationStore
<p>

</p>

## Statistics API
<p>

</p>

## Best Practices
<p>

</p>

## Examples with Africa-Is-Talking
<p>

</p>



