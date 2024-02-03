# Quick Dial
## _Building Ussd application the declarative way_ 

>Design necessity and philosophy:  **_Anything that can be built, can be built
>declaratively!_**

- [Dependency](#dependency)
- [Ussd Architecture](#ussd-architecture)
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

@UssdMenuHandler(menu = "1")
public class TestMenuHandler{


  @UssdSubMenuHandler
  public String showAccountOpeningMenus(UssdUserContext, SessionData, UssdSession){
  
  
}

```

## UssdExecution
<p>

</p>

## Interceptors
<p>

</p>

## Messaging
<p>

</p>

## Events
<p>

</p>


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



