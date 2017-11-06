require 'rubygems'
require 'pushmeup'
require 'json'

APNS.host = 'gateway.push.apple.com' 
#APNS.host = 'gateway.sandbox.push.apple.com' 
APNS.port = 2195 
APNS.pem  = 'wisersmart.pem'
APNS.pass = 'Password'
device_token = 'ba987a01c7da1d22e8be67e13b6a172198ef392cfe180f6387497b7d407a4fb4'
ar=["kitchen","14","dsd"]
al={:"loc-key"=>"FROST_PROTECTION_ALARM",:"loc-args"=> ar }
aps={:"alert"=> al}
APNS.send_notification(device_token,:alert=>al, :badge => 1, :sound => 'beep.wav')

