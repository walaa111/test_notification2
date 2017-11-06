require 'rubygems'
require 'pushmeup'
GCM.host = 'https://android.googleapis.com/gcm/send'
GCM.format = :json
GCM.key = "AIzaS23nNmx21ojsMqoJhtenGGt4JAn9BhTa0qQ"
destination = ["APA91bEBWuhdT8aFr1exUqicephXk2N0TJZgIK1RVy3Gf5Md_9a_1JGWbf3ka3bUOJ0HkQcziMvGg9gbIIe6ELduin1G0qzutENdrQEUVei8nCiLLZsyvOmaWsHVJ3n9PiRXVlgLkQHON6bQ_MOsb422WzjtFIhGHp1SQ95mcDZkwV3CBMBv6Xc"]
data = {
  :message=>"DEVICE_BATTERY_FAILED"
}
GCM.send_notification( destination, data)

