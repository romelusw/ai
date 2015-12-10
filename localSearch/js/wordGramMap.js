/**
 * Javascript implentation of an "n-gram" over words.
 *
 * @author romelw
 */
 "use-strict";
function GramHashMap() {
    var biGram = new Map();

    this.get = function(word) {
        var suggestions = new Array();
        var elem = biGram.get(hashFunc(word));
        if(elem !== undefined) {
            elem.uniGram.forEach(function(i, k) {
                suggestions.push({"word":i.word, "count":i.frequency});
            });
        }
        return suggestions.sort(function(a, b) {return b.count - a.count;});
    }

    this.add = function(key, val) {
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

    function hashFunc(key) {
        return btoa(key.toLowerCase());
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