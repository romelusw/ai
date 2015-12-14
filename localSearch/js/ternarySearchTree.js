/**
 * Javascript implentation of a ternary search tree as described within:
 * http://nlp.cs.berkeley.edu/pubs/Pauls-Klein_2011_LM_paper.pdf
 *
 * @author romelw
 */
 "use-strict";
function TST() {
    var root = undefined, size = 0;

    this.get = function(prefix, count) {
        var suggestions = new Array();
        var prefixRoot = get(root, prefix, 0);
        if(prefixRoot !== undefined) {
            matches(prefixRoot, prefix, suggestions, count);
        }
        return suggestions.sort(function(a, b) {return b.count - a.count;});
    }

    this.add = function(word) {
        if(word) {
            root = add(root, word, 0);
        }
    }

    this.size = function() {
        return size;
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

    function matches(currNode, prefix, results, count) {
        if(currNode === undefined) return;
        matches(currNode.left, prefix, results, count);
        matches(currNode.middle, prefix + currNode.character, results, count);
        matches(currNode.right, prefix, results, count);
        if(results.length < count && currNode.frequency > 0) {
            results.push({"word":prefix + currNode.character, "count":currNode.frequency});
        } else {
            return;
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

    // public String toString() {
    //     final StringBuilder sb = new StringBuilder();
    //     final String[] stack = new String[1000];
    //     Arrays.fill(stack, "");
    //     toString(root, stack, 0);
    //     for(int i = 0; i < stack.length; i++) {
    //         if(stack[i].isEmpty()) continue;
    //         sb.append(stack[i]).append("\n");
    //     }
    //     return sb.toString();
    // }

    // private void toString(final Node currNode, final String[] stack, int depth) {
    //     if(currNode == null) return;
    //     stack[depth] += String.format("  %s", currNode.getCharacter());
    //     // Left
    //     if(currNode.getLeft() == null) {
    //         stack[depth + 1] += "↙ ";
    //     } else {
    //         for(int i = depth; i >= 0; i--) {
    //             stack[i] = new String(new char[depth]).replace("\0", " ") + stack[i];
    //         }
    //         stack[depth + 1] += "/ ";
    //     }
    //     toString(currNode.getLeft(), stack, depth + 2);

    //     // Middle
    //     if(currNode.getMiddle() == null) {
    //         stack[depth + 1] += "⊥";
    //     } else {
    //         toString(currNode.getMiddle(), stack, depth + 2);
    //         if(currNode.getLeft() != null || currNode.getRight() != null) {
    //             for(int i = depth ; i >= depth; i--) {
    //                 stack[i] = new String(new char[depth]).replace("\0", " ") + stack[i];
    //             }
    //         }
    //         stack[depth + 1] += "|";
    //     }

    //     // Right
    //     if(currNode.getRight() == null) {
    //         stack[depth + 1] += " ↘";
    //     } else {
    //         stack[depth + 1] += " \\";
    //         for(int i = depth; i >= 0; i--) {
    //             stack[i] = new String(new char[depth]).replace("\0", " ") + stack[i];
    //         }
    //     }
    //     toString(currNode.getRight(), stack, depth + 2);
    // }
}