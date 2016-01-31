/**
 *  Low Battery Alert
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
 *  for the specific language governing permissions and -limitations under the License.
 *
 */
definition(
    name: "Low Battery Alert",
    namespace: "lojack66",
    author: "Louis Jackson",
    description: "Determines if the battery is below a given threshold in selected devices.",
    category: "My Apps",
    iconUrl: "http://cdn.device-icons.smartthings.com/Health%20&%20Wellness/health6-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Health%20&%20Wellness/health6-icn@2x.png",
    iconX3Url: "http://cdn.device-icons.smartthings.com/Health%20&%20Wellness/health6-icn@2x.png")


preferences {
    section("Select Things to Control:") {
            input "thebattery", "capability.battery", title: "When batteries in...", multiple: true,   required: true
    		input "minThreshold", "number",   title: "Are below... (default 40)%", defaultValue:40,   required: false
    }
  
    section("Via push notification and/or a SMS message") {
        input("recipients", "contact", title: "Send notifications to") {
            input "phone", "phone", title: "Warn with text message (optional)", description: "Phone Number", required: false
        }
    }
}

def installed() {
   	log.trace "(0A) ${app.label} - installed() - settings: ${settings}"
  	initialize()
}

def updated() {
	log.info "(0B) ${app.label} - updated()"
    unschedule() //un-schedule
	initialize()
}

def initialize() {
   	log.info "(0C) ${app.label} - initialize()"
    
    runEvery1Hour(doBatteryCheck)
	//schedule("0 30 11 ? * SAT", doBatteryCheck) // call handlerMethod2 at 11:30am every Saturday of the month
    
    doBatteryCheck() //Check now!
}

def doBatteryCheck() {
	log.trace "(0D) ${app.label} - doBatteryCheck() : ${settings}"
    
    def nDevBelow  = 0
    def strMessage = ""

	for (batteryDevice in thebattery) 
    {
    	def batteryLevel = batteryDevice.currentValue("battery")

        if ( batteryLevel <= settings.minThreshold.toInteger() ) 
        {
            log.warn "(0E) - current value for ${batteryDevice.label} is ${batteryLevel}"
			strMessage += "- ${batteryDevice.label}: ${batteryLevel}%.\n"
			nDevBelow++
        }
        else
			log.info "(0F) - current value for ${batteryDevice.label} is ${batteryLevel}"
    }

    if ( nDevBelow ){
    	send("The ${app.label} SmartApp determined you have ${nDevBelow} device(s) below the set battery alert level of ${settings.minThreshold.toInteger()}%:\n\n${strMessage}")
    }
}

private send(msg) {
    log.info "(01) sending message ${msg}"

	if (location.contactBookEnabled) 
    {
    	log.trace "(02) send to contact ${recipients}"
        sendNotificationToContacts(msg, recipients)  //Sends the specified message to the specified contacts and the Notifications feed.
    } 
    else 
    	sendNotificationEvent(msg)
        
    if (phone)
    {
        log.trace "(03) send to contact ${phone}"
        sendSms(phone, msg) //Sends the message as an SMS message to the specified phone number and displays it in Hello, Home. The message can be no longer than 140 characters.
    }   
}
