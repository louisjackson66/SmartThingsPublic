/**
 *  Reset Appliance 
 *
 *  Copyright 2016 Louis Jackson
 *
 *  Version 1.0.0   31 Jan 2016
 *
 *	Version History
 *
 *	1.0.0	28 Jan 2016		Added to GitHub
 *	1.0.0	27 Jan 2016		Creation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Reset Appliance",
    namespace: "lojack66",
    author: "Louis Jackson",
    description: "At a specific time, turn a device/appliance OFF and then back ON after a given number of seconds.",
    category: "My Apps",
    iconUrl: "http://cdn.device-icons.smartthings.com/Appliances/appliances17-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Appliances/appliances17-icn@2x.png",
    iconX3Url: "http://cdn.device-icons.smartthings.com/Appliances/appliances17-icn@2x.png")

preferences {
    page(name: "page1", uninstall: true, install: true)
}

def page1() {
    dynamicPage(name: "page1") {
    	section("Reset Thing(s) (Off/On)") {
    		input "switch1", "capability.switch", title: "Using switch(s)", multiple: true, required: true
        
        	input "bFurnace", "bool", title: "Reset every 3 hours?", required: false, defaultValue:false, submitOnChange: true
			if(!bFurnace) { input "time1", "time", title: "At this time of day", required: false }
        
        	input "seconds1", "number", title: "Turn on after (default 30) seconds", defaultValue:30, required: false
			
            label title: "Assign a name", required: false
            mode title: "Set for specific mode(s)", required: false
    	}
    }
}

def installed() 
{
	log.trace "(0A) ${app.label} - installed() - settings: ${settings}"
	initialize()
}

def updated() 
{
	log.info "(0B) ${app.label} - updated()"
    unschedule()
	initialize()
}

def initialize() 
{
    if (bFurnace) 
    {
    	log.info "(0C) ${app.label} - initialize() - Run every 3 hours"
        runEvery3Hours(handlerMethod)
        handlerMethod()                   //runs this SmartApp after inital setup
    } else
    {
    	log.info "(0D) ${app.label} - initialize() - Run as scheduled: ${time1}"
        schedule(time1, handlerMethod)
    }    
}

def handlerMethod() {
	log.trace "(0E) - ${app.label} - ${settings}"
    
    for (SwitchDeviceOff in switch1) 
    {
        log.info "(0F) - Turning OFF ${SwitchDeviceOff.label}"
		SwitchDeviceOff.off()
    }
    
    runIn(seconds1, turnOnSwitch)
}

def turnOnSwitch() {
	def strMsg = ""
    
    for (SwitchDeviceOn in switch1) 
    {
        log.info "(10) - Turning ON ${SwitchDeviceOn.label}"
		SwitchDeviceOn.on()
        strMsg += "[${SwitchDeviceOn.label}]"
    }
    
     sendNotificationEvent("${app.label} completed reset of ${strMsg}")
     log.trace "(11) - ${app.label} completed reset of ${strMsg} - ${settings}"
}
