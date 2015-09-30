"use strict";
// GLOBALS
var aiPlayedLastMove = false;
var TYPES = {
    AI: 0,
    HUMAN: 1,
    EMPTY: -1
};
var BOARD = {
    movesStack: [],
    markedSpaces: 0,
    grid: initGrid(),
    reset: function() {
        this.movesStack = [];
        this.markedSpaces = 0;
        this.grid = initGrid();
    },
    markSpace: function (x, y, type) {
        var piece = this.grid[x][y];
        if (!this.spaceOccupied(x, y)) {
            var htmlElem = $('#table section').eq(x).children().eq(y);
            piece.type = type;
            this.movesStack.push(piece);
            this.markedSpaces++;
            var text = piece.type == TYPES.AI ? "fa fa-laptop faa-tada animated" : "fa fa-user faa-tada animated";
            if(piece.type == TYPES.EMPTY) {
                text = "";
            }
            htmlElem.addClass("no_hover");
            htmlElem.html("<i class='" + text + "'></i>");
        }
    },
    isTied: function () {
        return this.markedSpaces == this.grid.length * this.grid.length;
    },
    typeWon: function (type) {
        var retVal = false;
        var GAME_WINNER_STRENGTH = 6;
        var lastMove = BOARD.lastMove(type);

        if (lastMove !== undefined) {
            if (horizontalStrength(lastMove, type) == GAME_WINNER_STRENGTH ||
                verticalStrength(lastMove, type) == GAME_WINNER_STRENGTH ||
                diagnolStrength(lastMove, type) == GAME_WINNER_STRENGTH) {
                retVal = true;
            }
        }
        return retVal;
    },
    getCenterPiece: function () {
        var center = Math.floor(this.grid.length / 2);
        return this.grid[center][center];
    },
    spaceOccupied: function (x, y) {
        return this.grid[x][y].type != TYPES.EMPTY;
    },
    isCornerPiece: function (x, y) {
        return x == y || Math.abs(x - y) == 2;
    },
    lastMove: function (type) {
        var retVal = undefined;
        for (var i = 0; i < this.movesStack.length; i++) {
            var move = this.movesStack[i];
            if (move.type == type) {
                retVal = move;
            }
        }
        return retVal;
    }
};

function BoardPiece(x, y, t) {
    this.type = t;
    this.x = x;
    this.y = y;
}

function initGrid() {
    var t0 = performance.now();
    var BOARD_SIZE = 3;
    var retVal = [BOARD_SIZE];
    for (var i = 0; i < BOARD_SIZE; i++) {
        retVal[i] = [BOARD_SIZE];
        for (var j = 0; j < BOARD_SIZE; j++) {
            retVal[i][j] = new BoardPiece(i, j, TYPES.EMPTY);
        }
    }
    var t1 = performance.now();
    console.log("Generating grid took: " + (t1 - t0) + "ms.");
    return retVal;
}

function evalGame() {
	if(BOARD.typeWon(TYPES.AI)) {
		alert("Artificial intelligence wins.");
		$("#table div").off("click");
		return;
	}

	if(BOARD.isTied()) {
		alert("Close, but not close enough.")
		$("#table div").off("click");
		return;
	}

	if(BOARD.typeWon(TYPES.HUMAN)) {
		confirm("Did you cheat?");
		$("#table div").off("click");
		return;
	}

	// Continue play
	if(aiPlayedLastMove) {
		playerMove(evalGame);
	} else {
        setTimeout(function(){ aiMove(evalGame); }, 300); // Wait for animation to complete
	}
}

function startGame() {
	if(Math.random() < 0.5) {
		aiMove(evalGame);
	} else {
		playerMove(evalGame);
	}
}

function aiMove(_callback) {
    var t0 = performance.now();
    var move = optimalMove();
    var t1 = performance.now();
    console.log("Finding optimal move took: " + (t1 - t0) + "ms.");
    BOARD.markSpace(move.x, move.y, TYPES.AI);
    aiPlayedLastMove = true;
    _callback();
}

function playerMove(_callback) {
    $('#table div').on("click", function() {
    	var parent = $(this).parent();
    	var col = parent.children().index($(this));
    	var row = parent.parent().children().index($(this).parent());
	    if (!BOARD.spaceOccupied(row, col)) {
	        BOARD.markSpace(row, col, TYPES.HUMAN);
	        $("#table div").off("click");
	        aiPlayedLastMove = false;
	        _callback();
	    }
    });
}
$("button#restart").click(function() {
    console.clear();
    BOARD.reset();
    $('#table section div').each(function() {  
        $(this).empty().removeClass(); 
    });
    startGame();
});
startGame();