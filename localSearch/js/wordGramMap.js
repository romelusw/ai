/**
 * Javascript implentation of an "n-gram" over words.
 *
 * @author romelw
 */
 "use-strict";
function GramHashMap() {
    var biGram = new Map();

    this.get = function(word, count) {
        var suggestions = new Array();
        var hash = hashFunc(word)
        var elem = biGram.get(hash);
        if(elem) {
            for(var i = 0; i < elem.uniGram.length; i++) {
                var word = elem.uniGram[i];
                if(suggestions.length < count) {
                    suggestions.push({"word":word.word, "count":word.frequency});
                } else {
                    break;
                }
            }
        }
        return suggestions.sort(function(a, b) {return b.count - a.count;});
    }

    this.add = function(key, val) {
        if(key) {
            var hash = hashFunc(key);
            if (biGram.get(hash) === undefined) {
                biGram.set(hash, new GramNode(key));
            } else {
                biGram.get(hash).frequency++;
            }

            if (val !== undefined) {
                biGram.get(hash).add(val);
            }
        }
    }

    this.size = function() {
        return biGram.size;
    }

    function hashFunc(key) {
        var newKey;
        try {
            newKey = btoa(key.toLowerCase());
        }
        catch(err) {
            console.log("Failed to encode:", key);
        }
        return newKey;
    }

    function GramNode(word) {
        this.frequency = 1;
        this.word = word;
        this.uniGram = new Map();
        this.add = function(key) {
            var hash = hashFunc(key);
            if(this.uniGram.get(hash) === undefined) {
                this.uniGram.set(hash, new GramNode(key));
            } else {
                this.uniGram.get(hash).frequency++;
            }
        }
    }
}
