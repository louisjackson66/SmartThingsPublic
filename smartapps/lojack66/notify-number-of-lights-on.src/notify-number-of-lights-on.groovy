/**
 *  Notify Number of Lights On
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
    name: "Notify Number of Lights On",
    namespace: "lojack66",
    author: "Louis Jackson",
    description: "Send a notification of the number of lights on.  This feature use to be part of the dashboard.",
    category: "My Apps",
    iconUrl: "http://cdn.device-icons.smartthings.com/Lighting/light11-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Lighting/light11-icn@2x.png",
    iconX3Url: "http://cdn.device-icons.smartthings.com/Lighting/light11-icn@2x.png")


preferences 
{
	section() {
		input "switches", "capability.switch", title: "Select Lights...", multiple: true, required:true
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
	unsubscribe()
	initialize()
}

def initialize() 
{
	log.info "(0C) ${app.label} - initialize()"
    //runEvery1Hour(onHandler)
    //onHandler() // Run immediately
    
    subscribe(switches, "switch", onHandler)
}

//def onHandler()
def onHandler(evt) 
{
    def currSwitches = switches.currentSwitch  // returns a list of the values for all switches
	def strMessage = ""
    
    def onSwitches = currSwitches.findAll { switchVal ->
        switchVal == "on" ? true : false
    }

	if (onSwitches.size())
    {
		strMessage = "${onSwitches.size()} out of ${switches.size()} lights are on."
    	log.warn "${strMessage} - ${currSwitches} - ${onSwitches} - ${switchVal}"
    	sendNotificationEvent(strMessage)
    }
}
