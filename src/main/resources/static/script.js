//colorToCodeMapping
const colorToCodeMap = new Map();
colorToCodeMap.set("background-color:green;", "G");
colorToCodeMap.set("background-color:orange;", "Y");
colorToCodeMap.set("background-color:gray;", "B");

function checkRadio(name, value) {
    console.log("checkRadio: name=" + name + " value=" + value);

    //set the color of row0 cell corresponding to name and color corresponding to value
    //name
    var target = name.substring(0, 2);

    var color = "white";
    if (value == 'G') {
        color = "green";
    } else if (value == 'Y') {
        color = "orange";
    } else if (value == 'B') {
        color = "gray";
    }
    document.getElementById(target).setAttribute("style", "background-color:" + color + ";");
}

function deleteRow(row) {
    console.log("deleteRow:: " + row);
    document.getElementById("myTable").removeChild(document.getElementById(row));
}

function submitQuery() {
    //Read radio value
    displayRadioValue();

    //validations

    //Read table
    var json = readInputTable();
    if (json == null) {
        return;
    }
    console.log("generated json: " + JSON.stringify(json));

    //Now, clone and add an empty row
    //cloneRow();

    //create an AJAX request
    solve(json);
}

function solve(json) {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            console.log("Response: " + this.responseText);
            //document.getElementById("demo").innerHTML = this.responseText;
            jsonResponse = JSON.parse(this.responseText);
            if (jsonResponse.length > 0) {
                cloneRow(jsonResponse[0].word);

                //update message
                var msg1 = "";
                var msg2 = "";
                if (jsonResponse.length > 1) {
                    msg1 = "There are " + jsonResponse.length + " possible solutions for the given input.";
                    msg2 = "The most probable word '" + jsonResponse[0].word + "' has already been inserted above into a new row. If you want to proceed with the suggested word, enter that in your wordle app and duplicate the color hints provided by the game here to proceed.";
                }
                if (jsonResponse.length == 1) {
                    msg1 = "There is only 1 possible solution for the given input.";
                    msg2 = "This might be the solution to your wordle.";
                }

                document.getElementById('m1').innerHTML = msg1;
                document.getElementById('m2').innerHTML = msg2;
            } else {
                alert("No solution exists for the given input. Please try again.");
                return;
            }
        }
    };
    xhttp.open("POST", "/solve", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(JSON.stringify(json));
}

function readInputTable() {
    var table = document.getElementById("myTable");
    var totalRows = document.getElementById("myTable").rows.length;

    let json = {};

    //iterate through all the data rows
    for (var i = 0; i < totalRows - 1; i++) {
        var row = table.rows[i];
        var totalCols = row.cells.length;
        //iterate through all the columns
        let word = "";
        let color = "";
        for (var j = 0; j < totalCols - 1; j++) {
            var cell = row.cells[j];
            let inputs = cell.getElementsByTagName('input');
            console.log("i=" + i + " j=" + j + " " + inputs[0].value + " color:" + colorToCodeMap.get(inputs[0].getAttribute("style")));
            if (colorToCodeMap.get(inputs[0].getAttribute("style")) === undefined) {
                alert("Missing data: color not set at row:" + i + " column:" + j);
                return null;
            }
            if (inputs[0].value === undefined || inputs[0].value.length != 1) {
                alert("Missing data: letter not set at row:" + i + " column:" + j);
                return null;
            }

            word += inputs[0].value;
            color += colorToCodeMap.get(inputs[0].getAttribute("style"));
        }

        if (word.length != 5) {
            alert("word " + word + " is mal formed");
            return null;
        }
        if (color.length != 5) {
            alert("color " + color + "is mal formed");
            return null;
        }

        //add word:color key/value pair
        json[word] = color;
    }

    return json;
}

function displayRadioValue() {
    for (var i = 1; i <= 5; i++) {
        var id = "c" + i + "Color";
        var ele = document.getElementsByName(id);

        for (j = 0; j < ele.length; j++) {
            if (ele[j].checked) {
                console.log("id:" + id + " " + ele[j].value);
            }
        }
    }
}

function resetRadios() {
    for (var i = 1; i <= 5; i++) {
        var name = "c" + i + "Color";
        var ele = document.getElementsByName(name);
        console.log("size of ele:" + ele.length);
        for (j = 0; j < ele.length; j++) {
            console.log("radio name:" + name + " " + ele[j].checked);
            ele[j].checked = false;
        }
    }
}

function cloneRow(topSuggestedWord) {
    var totalRows = document.getElementById("myTable").rows.length;

    console.log("totalRows=" + totalRows);
    // for(var i : totalRows){
    //     console.log("Row:"+i);
    //     //console.log(document.getElementById("row"+(i)).childNodes);
    // }

    //copy row0 into new row
    var rowToCopy = "row0";
    var idOfNewRow = "row" + (totalRows - 1);
    console.log("row to copy:" + rowToCopy + " id of new row:" + idOfNewRow);

    var row = document.getElementById(rowToCopy); // find row to copy
    var table = document.getElementById("myTable"); // find table to append to
    var clone = row.cloneNode(true); // copy children too
    clone.id = idOfNewRow; // change id or other attributes/contents
    //table.appendChild(clone); // add new row to end of table

    var children = clone.children;
    //console.log("type of children: "+children.type);
    for (var i = 0; i < children.length; i++) {
        var child = children[i];
        let inputs = child.getElementsByTagName('input');
        inputs[0].setAttribute("id", "frozen" + i);

        console.log(child.id);
        console.log("child:" + i + " " + child.innerHTML);
        // Do stuff.

        if (i == children.length - 1) {
            //child[i].rowspan = "1";
            child.setAttribute("rowspan", 1);
            child.setAttribute("background-color", "red");
            let inputs = child.getElementsByTagName('input');
            inputs[0].setAttribute("value", '\u{274C}')
            inputs[0].setAttribute("onClick", "deleteRow('" + idOfNewRow + "')");
        }
    }

    var suggestedWordArray = ['', '', '', '', ''];
    if (topSuggestedWord != undefined && topSuggestedWord.length == 5) {
        suggestedWordArray = Array.from(topSuggestedWord);
    }

    //reset row 0
    var row0Row = document.getElementById("row0").children;
    for (var i = 0; i < row0Row.length - 1; i++) {

        console.log("row0Row:i=" + i + " " + row0Row[i].innerHTML);
        let inputs = row0Row[i].getElementsByTagName('input');
        // for (let index = 0; index < inputs.length; ++index) {
        //     if(inputs[index].type =="text") {
        //         inputs[index].value = '';
        //     }
        // }
        inputs[0].value = suggestedWordArray[i];
        //inputs[0].setAttribute("style", "background-color:white;");
        inputs[0].setAttribute("style", "background-color:transparent;");
        inputs[0].removeAttribute("style");
    }

    //reset radios
    resetRadios();

    //finally insert into table
    table.insertBefore(clone, table.children[totalRows - 2]);

    var cloneChild = document.getElementById(idOfNewRow).childNodes;
    console.log(cloneChild)

    //reset rowToCopy
    console.log(table.rows[rowToCopy].innerHTML);

    //hide the search button from the previous row
}