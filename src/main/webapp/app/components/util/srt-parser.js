/**
 * Created by crocop on 11.05.16.
 */
(function() {
    /*jshint bitwise: false*/
    'use strict';

    angular
        .module('stageCapServiceApp')
        .factory('SrtParser', SrtParser);

    function SrtParser () {

        var parser = (function() {
            var pItems = {};

            /**
             * Converts SubRip subtitles into array of objects
             * [{
     *     id:        `Number of subtitle`
     *     startTime: `Start time of subtitle`
     *     endTime:   `End time of subtitle
     *     text: `Text of subtitle`
     * }]
             *
             * @param  {String}  data SubRip suntitles string
             * @param  {Boolean} ms   Optional: use milliseconds for startTime and endTime
             * @return {Array}
             */
            pItems.fromSrt = function(data, ms) {
                var useMs = ms ? true : false;

                data = data.replace(/\r/g, '');
                var regex = /(\d+)\n(\d{2}:\d{2}:\d{2},\d{3}) --> (\d{2}:\d{2}:\d{2},\d{3})/g;
                data = data.split(regex);
                data.shift();

                var items = [];
                for (var i = 0; i < data.length; i += 4) {
                    items.push({
                        id: data[i].trim(),
                        startTime: useMs ? timeMs(data[i + 1].trim()) : data[i + 1].trim(),
                        endTime: useMs ? timeMs(data[i + 2].trim()) : data[i + 2].trim(),
                        text: data[i + 3].trim()
                    });
                }

                return items;
            };

            /**
             * Converts Array of objects created by this module to SubRip subtitles
             * @param  {Array}  data
             * @return {String}      SubRip subtitles string
             */
            pItems.toSrt = function(data) {
                if (!data instanceof Array) return '';
                var res = '';

                for (var i = 0; i < data.length; i++) {
                    var s = data[i];

                    if (!isNaN(s.startTime) && !isNaN(s.endTime)) {
                        s.startTime = msTime(parseInt(s.startTime, 10));
                        s.endTime = msTime(parseInt(s.endTime, 10));
                    }

                    res += s.id + '\r\n';
                    res += s.startTime + ' --> ' + s.endTime + '\r\n';
                    res += s.text.replace('\n', '\r\n') + '\r\n\r\n';
                }

                return res;
            };

            var timeMs = function(val) {
                var regex = /(\d+):(\d{2}):(\d{2}),(\d{3})/;
                var parts = regex.exec(val);

                if (parts === null) {
                    return 0;
                }

                for (var i = 1; i < 5; i++) {
                    parts[i] = parseInt(parts[i], 10);
                    if (isNaN(parts[i])) parts[i] = 0;
                }

                // hours + minutes + seconds + ms
                return parts[1] * 3600000 + parts[2] * 60000 + parts[3] * 1000 + parts[4];
            };

            var msTime = function(val) {
                var measures = [ 3600000, 60000, 1000 ];
                var time = [];

                for (var i in measures) {
                    var res = (val / measures[i] >> 0).toString();

                    if (res.length < 2) res = '0' + res;
                    val %= measures[i];
                    time.push(res);
                }

                var ms = val.toString();
                if (ms.length < 3) {
                    for (i = 0; i <= 3 - ms.length; i++) ms = '0' + ms;
                }

                return time.join(':') + ',' + ms;
            };

            return pItems;
        })();

// ignore exports for browser
        if (typeof exports === 'object') {
            module.exports = parser;
        }
        //this is not really nice. Basically the whole source is captured in a variable
        //source is taken from here https://github.com/bazh/subtitles-parser
        //TODO we should import it through package manager
        // var parser=function(){var r={};r.fromSrt=function(r,e){var n=e?!0:!1;r=r.replace(/\r/g,"");var i=/(\d+)\n(\d{2}:\d{2}:\d{2},\d{3}) --> (\d{2}:\d{2}:\d{2},\d{3})/g;r=r.split(i),r.shift();for(var a=[],d=0;d<r.length;d+=4)a.push({id:r[d].trim(),startTime:n?t(r[d+1].trim()):r[d+1].trim(),endTime:n?t(r[d+2].trim()):r[d+2].trim(),text:r[d+3].trim()});return a},r.toSrt=function(r){if(!r instanceof Array)return"";for(var t="",n=0;n<r.length;n++){var i=r[n];isNaN(i.startTime)||isNaN(i.endTime)||(i.startTime=e(parseInt(i.startTime,10)),i.endTime=e(parseInt(i.endTime,10))),t+=i.id+"\r\n",t+=i.startTime+" --> "+i.endTime+"\r\n",t+=i.text.replace("\n","\r\n")+"\r\n\r\n"}return t};var t=function(r){var t=/(\d+):(\d{2}):(\d{2}),(\d{3})/,e=t.exec(r);if(null===e)return 0;for(var n=1;5>n;n++)e[n]=parseInt(e[n],10),isNaN(e[n])&&(e[n]=0);return 36e5*e[1]+6e4*e[2]+1e3*e[3]+e[4]},e=function(r){var t=[36e5,6e4,1e3],e=[];for(var n in t){var i=(r/t[n]>>0).toString();i.length<2&&(i="0"+i),r%=t[n],e.push(i)}var a=r.toString();if(a.length<3)for(n=0;n<=3-a.length;n++)a="0"+a;return e.join(":")+","+a};return r}();"object"==typeof exports&&(module.exports=parser);

//var dataMs = parser.fromSrt(srt, true);
//         var service = {
//            fromSrt:function(){}
//         };

        return parser;



    }
})();
