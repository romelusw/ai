/**
 * Javascript implentation of a ternary search tree as described within:
 * http://nlp.cs.berkeley.edu/pubs/Pauls-Klein_2011_LM_paper.pdf
 *
 * @author romelw
 */
 "use-strict";
function TST() {
    var root = undefined, size = 0;

    this.get = function(prefix) {
        var suggestions = new Array();
        var prefixRoot = get(root, prefix, 0);
        if(prefixRoot !== undefined) {
            matches(prefixRoot, prefix, suggestions);
        }
        return suggestions.sort(function(a, b) {return b.count - a.count;});
    }

    this.add = function(word) {
        root = add(root, word, 0);
        var j = 3;
    }

    function get(currNode, prefix, index) {
        var charAtIndex = prefix.charCodeAt(index);
        var retVal = currNode;
        if(currNode === undefined) return undefined;

        if(charAtIndex < currNode.charCode) { // Left
            retVal = get(currNode.left, prefix, index);
        } else if(charAtIndex > currNode.charCode) { // Right
            retVal = get(currNode.right, prefix, index);
        } else if(index < prefix.length - 1) { // Middle
            retVal = get(currNode.middle, prefix, ++index);
        } else {
            retVal = currNode.middle;
        }
        return retVal;
    }

    function add(currNode, word, index) {
        var charAtIndex = word.charCodeAt(index);
        if(currNode === undefined) currNode =  new Node(word[index]);

        if(charAtIndex < currNode.charCode) {
            currNode.left = add(currNode.left, word, index);
        } else if(charAtIndex > currNode.charCode) {
            currNode.right = add(currNode.right, word, index);
        } else if(index < word.length - 1) {
            currNode.middle = add(currNode.middle, word, ++index);
        } else {
            if(currNode.frequency == 0) { // Tally unique words
                size++;
            }
            currNode.frequency++;
        }
        return currNode;
    }

    function matches(currNode, prefix, results) {
        if(currNode === undefined) return;
        matches(currNode.left, prefix, results);
        matches(currNode.middle, prefix + currNode.character, results);
        matches(currNode.right, prefix, results);
        if(currNode.frequency > 0) {
            results.push({"word":prefix + currNode.character, "count":currNode.frequency});
        }
    }

    function Node(char) {
        this.frequency = 0;
        this.character = char;
        this.charCode = char.charCodeAt(0);
        this.left = undefined;
        this.middle = undefined;
        this.right = undefined;
    }
}