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
    var json = readInputTable(document.getElementById("myTable").rows.length-1);
    if (json == null) {
        return;
    }
    console.log("generated json: " + JSON.stringify(json));

    //Now, clone and add an empty row
    //cloneRow();

    //create an AJAX request
    solve(json, false);
}

window.addEventListener('load', function() {
    initialization();
})

function initialization(){
    console.log("Initialization invoked");
    var json = {};
    solve(json, true);
}

function solve(json, init) {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            console.log("Response: " + this.responseText);
            //document.getElementById("demo").innerHTML = this.responseText;
            try{
                jsonResponse = JSON.parse(this.responseText);
            }
            catch (err){
                console.log("No response from server");
                cloneRow("-----");
                //alert("No solution exists for this. Report.");
                return;
            }
            if (jsonResponse.length > 0) {
                if(init==true){
                    initRow(jsonResponse[0].word);
                }
                else {
                    cloneRow(jsonResponse[0].word);
                }

                //update message
                var msg1 = "";
                var msg2 = "";
                if (jsonResponse.length > 1) {
                    msg1 = "There are " + jsonResponse.length + " possible solutions for the given input.";
                    msg2 = "The most probable word '" + jsonResponse[0].word + "' has already been inserted above into a new row. If you want to proceed with the suggested word, enter that in your wordle app and duplicate the color hints provided by the game here to proceed.";
                }
                if (jsonResponse.length == 1) {
                    // msg1 = "There is only 1 possible solution for the given input.";
                    // msg2 = "This might be the solution to your wordle.";
                    msg1 = "The next-best guess has been fetched and populated above."
                    msg2 = "Please use this word in your Wordle app and add the colors suggested by Wordle app to the above row."
                }

                document.getElementById('m1').innerHTML = msg1;
                document.getElementById('m2').innerHTML = msg2;

                //update table
                generateTable(jsonResponse);
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

const excludedWords = new Set();
function discardTheCurrentSuggestionAndRequestNew(){

    //add current word from row0 into excluded words
    let word = "";

    var table = document.getElementById("myTable");
    var totalRows = document.getElementById("myTable").rows.length;
    var i = totalRows-2;
    var totalCols = 6;
    for (var j = 0; j < totalCols-1; j++) {
        var row = table.rows[i];
        var cell = row.cells[j];
        let inputs = cell.getElementsByTagName('input');

        if (inputs[0].value === undefined || inputs[0].value.length != 1) {
            alert("Missing data: letter not set at row:" + i + " column:" + j);
            return null;
        }

        word += inputs[0].value;

    }

    if (word.length != 5) {
        alert("word " + word + " is mal formed");
        return null;
    }

    //replace word with question marks
    initRow("?????");

    //add word to the exclusion list
    excludedWords.add(word);

    //read input table
    var json = readInputTable(document.getElementById("myTable").rows.length-2);

    console.log("json="+JSON.stringify(json));

    if(json==null){
        json={};
    }

    //remove excluded words from json
    for(word in excludedWords){
        delete json[word];
    }

    //add all exculsions
    let result=[...excludedWords].join(' ');
    json['exclusions'] = result;

    //alert("Feature implementation is WIP. totalRows="+totalRows +" targetRowIdx="+i + " word="+word + " json="+JSON.stringify(json));
    solve(json, true);
}

function generateTable(json) {

    //Create a HTML Table element.
    var table = document.createElement("TABLE");
    table.border = "1";

    //Get the count of columns.
    var columnCount = 3;

    let headerRow = ["Word", "Score", "Rank"];
    //Add the header row.
    var row = table.insertRow(-1);
    for (var i = 0; i < columnCount; i++) {
        var headerCell = document.createElement("TH");
        headerCell.innerHTML = headerRow[i];
        row.appendChild(headerCell);
    }

    //Add the data rows.
    for (var i = 0; i < json.length; i++) {
        row = table.insertRow(-1);
        for (var j = 0; j < columnCount; j++) {
            var cell = row.insertCell(-1);
            if(j==0){
                cell.innerHTML = json[i].word;
            }
            else if(j==1){
                cell.innerHTML = Math.round(json[i].score * 1000) / 1000;
            }
            else if(j==2){
                cell.innerHTML = json[i].rank;
            }
        }
    }

    var dvTable = document.getElementById("Layer1");
    dvTable.innerHTML = "";
    dvTable.appendChild(table);
}

function readInputTable(rowsToRead) {
    var table = document.getElementById("myTable");
    var totalRows = rowsToRead;

    let json = {};

    //iterate through all the data rows
    for (var i = 0; i < totalRows; i++) {
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

function initRow(topSuggestedWord){
    var suggestedWordArray = ['', '', '', '', ''];
    if (topSuggestedWord != undefined && topSuggestedWord.length == 5) {
        suggestedWordArray = Array.from(topSuggestedWord);
    }

    //reset row 0
    var row0Row = document.getElementById("row0").children;
    for (var i = 0; i < row0Row.length - 1; i++) {

        console.log("row0Row:i=" + i + " " + row0Row[i].innerHTML);
        let inputs = row0Row[i].getElementsByTagName('input');
        inputs[0].value = suggestedWordArray[i];
        //inputs[0].setAttribute("style", "background-color:white;");
        inputs[0].setAttribute("style", "background-color:transparent;");
        inputs[0].removeAttribute("style");
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
            inputs[0].setAttribute("style", "text-align:center; width: 100%; height: 100%; font-size: medium");
            inputs[0].setAttribute("title","Delete this entry");
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