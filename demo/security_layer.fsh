@/* Forge Script - Enables authentication and authorization using PicketLink */;

@/* Detect Forge version */;
@v = SHELL.getEnvironment().getRuntimeVersion();

@/* Clear the screen */;
clear;

@/* This means less typing. If a script is automated, or is not meant to be interactive, use this command */; 
set ACCEPT_DEFAULTS true;

@/* Ask the user whether they want to patch in the changes described in the tutorial */;

set ACCEPT_DEFAULTS false;

if ( SHELL.promptBoolean("Apply security changes described in tutorial?") ) {
    echo Applying manual changes described in tutorial based on security_layer_tests.patch;
    git apply -v --ignore-whitespace --ignore-space-change patches/security_layer_tests.patch;
}

if ( SHELL.promptBoolean("Deploy to JBoss AS 7?") ) {
    @/* Deploy this to JBoss AS 7 to see the result */;
    build clean package jboss-as:deploy;

    echo Examine the app so far at http://localhost:8080/ticket-monster/faces/admin/index.xhtml;

} else {
    build clean package;
}