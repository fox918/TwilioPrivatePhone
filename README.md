# Simple personal Twilio PBX

This is a simple project to use twilio
as a simple personal phone interchange.

The interchange should provide the following functionality:
- Allow outgoing calls from SIP clients
- When a call is incoming:
    - Try to call the registered sip clients
    - Forward the call to my real phone
  
## Short functional description 
This code runs a small ktor web application that is bundled
via docker to a runnable container. The web application provides
simple PBX functionality by answering with TwiML snippets when sent
requests by Twilio because of incoming calls.

## Setup
I purchased a local phone number on twilio and setup the following ressources:

### Phone number
The phone number displayed in outgoing call and that is being called by 
external users. It should be set as ``phone.mainNumber`` for the  PBX 
to set the correct caller id on outgoing calls. 
It is configured to refer to ``/incoming`` to accept calls. 

### SIP Domain
My sip client registers to this sip domain. This domain is configured in the
config file under ``phone.sipClientUrl`` as ``client@sipDomain.ch``.

Call control is configured to refer to ``/outgoing`` for *incoming* calls of the domain.
This is because *incoming* calls refers to calls coming in from the sip clients
to the sip domain.

### Docker bundling
First assemble the runtime by running grade ``installDist`` and then bundle using docker
``docker build -t personal-pbx .``





