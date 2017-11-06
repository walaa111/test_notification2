cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "id": "com.eh.plugins.PushPlugin.PushNotification",
        "file": "plugins/com.eh.plugins.PushPlugin/Example/www/PushNotification.js",
        "pluginId": "com.eh.plugins.PushPlugin",
        "clobbers": [
            "PushNotification"
        ]
    },
    {
        "id": "cordova-plugin-inappbrowser.inappbrowser",
        "file": "plugins/cordova-plugin-inappbrowser/www/inappbrowser.js",
        "pluginId": "cordova-plugin-inappbrowser",
        "clobbers": [
            "cordova.InAppBrowser.open",
            "window.open"
        ]
    },
    {
        "id": "admob.admob",
        "file": "plugins/admob/www/admob.js",
        "pluginId": "admob",
        "clobbers": [
            "window.admob"
        ]
    },
    {
        "id": "pushbots-cordova-plugin.PushbotsPlugin",
        "file": "plugins/pushbots-cordova-plugin/www/pushbots.js",
        "pluginId": "pushbots-cordova-plugin",
        "clobbers": [
            "PushbotsPlugin"
        ]
    }
];
module.exports.metadata = 
// TOP OF METADATA
{
    "com.eh.plugins.PushPlugin": "1.0.8",
    "cordova-plugin-inappbrowser": "1.7.1-dev",
    "cordova-plugin-whitelist": "1.3.2",
    "admob": "5.5.0",
    "pushbots-cordova-plugin": "1.4.7"
};
// BOTTOM OF METADATA
});