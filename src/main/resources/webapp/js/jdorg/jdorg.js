/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function Organizer() {

    this.currentIndex = 0,
            this.currentType = 'TV_SHOW',
            this.shows = function () {
                this.header('show');
                this.currentType = 'TV_SHOW';
                this.currentIndex = 0;
                this.get(10);
            },
            this.movies = function () {
                this.header('movie');
                this.currentType = 'MOVIE';
                this.currentIndex = 0;
                this.get(10);
            }

    this.next = function (count) {
        this.get(count);
    },
            this.prev = function (count) {
                this.currentIndex -= count;
                this.get(count);
            },
            this.get = function (count) {
                this.getItems(this.currentType, this.currentIndex, count);
                this.currentIndex += count;
            },
            this.header = function (type) {
                var value = '';
                if (type == 'log') {
                    value = "<tr><th> # </th><th> Level </th><th> Message </th><th> Timestamp </th></tr>";
                } else if (type == 'movie') {
                    value = "<th>#</th><th>Movie</th><th>Timestamp</th><th>File</th>";
                } else if (type == 'show') {
                    value = "<th>#</th><th>Show</th><th>Season</th><th>Episode</th><th>Timestamp</th><th>File</th>";
                }

                $('#table_head')[0].innerHTML = value;

            }

    this.getItems = function (type, index, count) {
        var cType = this.currentType;
        $.get('/api/organizer/audit/list/' + type + '/' + index + "/" + count + "/",
                function (data) {
                    if (data.result) { 
                        var value = '<tbody id="table_result">';
                        var i = 1;
                        for (var item in data.items) {
                            value += "<tr>";
                            value += "<td>" + i + "</td>";
                            value += "<td>" + data.items[i - 1].show + "</td>";
                            if (cType == "TV_SHOW") {
                                value += "<td>" + data.items[i - 1].season + "</td>";
                                value += "<td>" + data.items[i - 1].episode + "</td>";
                            }
                            value += "<td>" + data.items[i - 1].timestamp + "</td>";
                            value += "<td>" + data.items[i - 1].filename + "</td>";
                            value += "</tr>";
                            i++;
                        }
                        value += '</tbody>';
                        $('#table_result')[0].innerHTML = value;
                    }
                }, 'json');
    },
            this.logs = function () {
                this.header('log');
                var index = 0;
                var count = 10;
                $.get('/api/organizer/logs/' + index + "/" + count + "/",
                        function (data) {
                            if (data.result) {
                                var value = '<tbody id="table_result">';
                                var i = 1;
                                for (var item in data.items) {
                                    value += "<tr>";
                                    value += "<td>" + i + "</td>";
                                    value += "<td>" + data.items[i - 1].level + "</td>";
                                    value += "<td>" + data.items[i - 1].message + "</td>";
                                    value += "<td>" + data.items[i - 1].timestamp + "</td>";
                                    value += "</tr>";
                                    i++;
                                }
                                value += '</tbody>';
                                $('#table_result')[0].innerHTML = value;
                            }
                        }, 'json');
            };
}
