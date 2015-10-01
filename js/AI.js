"use strict";

// T(n) = O(n^2)
function optimalMove() {
    var CLOSE_TO_WIN = 5;
    var TRICKY_MOVE = 9;
    var aiWinMove, humanWinMove, trickyMove;
    var optimalMove = { piece: undefined, strength: 0 };
    var center = BOARD.getCenterPiece();
    var ai_horizontal_strength, ai_vertical_strength, ai_diagnol_strength,
    human_horizontal_strength, human_vertical_strength, human_diagnol_strength;

    // Always choose a corner if starting
    if(BOARD.markedSpaces == 0) {
        var piece = randomCorner();
        optimalMove.piece = piece;
    } else if (!BOARD.spaceOccupied(center.x, center.y)) { // Choose center if defending
        optimalMove.piece = center;
    } else {
        // Find an optimal move ...
        var humanLastMove = BOARD.lastMove(TYPES.HUMAN);
        for (var i = 0; i < BOARD.grid.length; i++) {
            for (var j = 0; j < BOARD.grid.length; j++) {
                var piece = BOARD.grid[i][j];
                if (piece.type === TYPES.EMPTY) {
                    // AI can win now
                    if((ai_horizontal_strength = horizontalStrength(piece, TYPES.AI)) == CLOSE_TO_WIN ||
                        (ai_vertical_strength = verticalStrength(piece, TYPES.AI)) == CLOSE_TO_WIN ||
                        (ai_diagnol_strength = diagnolStrength(piece, TYPES.AI)) == CLOSE_TO_WIN) {
                        aiWinMove = piece;
                    }
                    // Human can win now
                    if((human_horizontal_strength = horizontalStrength(piece, TYPES.HUMAN)) == CLOSE_TO_WIN ||
                        (human_vertical_strength = verticalStrength(piece, TYPES.HUMAN)) == CLOSE_TO_WIN ||
                        (human_diagnol_strength = diagnolStrength(piece, TYPES.HUMAN)) == CLOSE_TO_WIN) {
                        humanWinMove = piece;
                    }
                    // // Strong move ahead
                    if((human_horizontal_strength + human_vertical_strength + human_diagnol_strength) == TRICKY_MOVE) {
                        trickyMove = piece;
                    }
                    // Base case
                    if ((ai_horizontal_strength + ai_vertical_strength + ai_diagnol_strength) > optimalMove.strength) {
                        optimalMove.piece = piece;
                        optimalMove.strength = ai_horizontal_strength + ai_vertical_strength + ai_diagnol_strength;
                    }
                }
            }
        }
    }

    // Now that you have determined all posibilities, choose the strategy with the best outcome
    if(aiWinMove !== undefined) {
        optimalMove.piece = aiWinMove;
    } else if(humanWinMove !== undefined) {
        optimalMove.piece = humanWinMove;
    } else if(trickyMove !== undefined) {
        optimalMove.piece = trickyMove;
    }

    return optimalMove.piece;
}

function horizontalStrength(board_piece, user) {
    var center = BOARD.getCenterPiece();
    var strength = tallyStrength(board_piece, user);
    if (board_piece.x == center.x) { // Center + Non diagnols
        strength += tallyStrength(BOARD.grid[board_piece.x - 1][board_piece.y], user) +
        tallyStrength(BOARD.grid[board_piece.x + 1][board_piece.y], user);
    } else {
        var increment = board_piece.x === 0 ? 1 : -1; // Ends
        strength += tallyStrength(BOARD.grid[board_piece.x + increment][board_piece.y], user) +
        tallyStrength(BOARD.grid[board_piece.x + (increment * 2)][board_piece.y], user);
    }
    return strength;
}

function verticalStrength(board_piece, user) {
    var center = BOARD.getCenterPiece();
    var strength = tallyStrength(board_piece, user);
    if (board_piece.y == center.y) { // Center + Non diagnols
        strength += tallyStrength(BOARD.grid[board_piece.x][board_piece.y - 1], user) +
        tallyStrength(BOARD.grid[board_piece.x][board_piece.y + 1], user);
    } else {
        var increment = board_piece.y === 0 ? 1 : -1; // Ends
        strength += tallyStrength(BOARD.grid[board_piece.x][board_piece.y + increment], user) +
        tallyStrength(BOARD.grid[board_piece.x][board_piece.y + (increment * 2)], user);
    }
    return strength;
}

function diagnolStrength(board_piece, user) {
    var center = BOARD.getCenterPiece();
    var strength = tallyStrength(board_piece, user);
    if ((board_piece.x == center.x && board_piece.y != center.y) || (board_piece.y == center.y && board_piece.x != center.x)) { // Non Diagnols
        strength = 0;
    } else if (board_piece.x == center.x && board_piece.y == center.y) { // Center
        strength += Math.max((tallyStrength(BOARD.grid[board_piece.x - 1][board_piece.y - 1], user) + tallyStrength(BOARD.grid[board_piece.x + 1][board_piece.y + 1], user)), 
            (tallyStrength(BOARD.grid[board_piece.x - 1][board_piece.y + 1], user) + tallyStrength(BOARD.grid[board_piece.x + 1][board_piece.y - 1], user)));
    } else {
        var xIncrement = board_piece.x === 0 ? 1 : -1;
        var yIncrement = board_piece.y === 0 ? 1 : -1;
        strength += tallyStrength(BOARD.grid[board_piece.x + xIncrement][board_piece.y + yIncrement], user) +
        tallyStrength(BOARD.grid[board_piece.x + (xIncrement * 2)][board_piece.y + (yIncrement * 2)], user);
    }
    return strength;
}

function tallyStrength(board_piece, type) {
    var retVal = 0;
    switch (board_piece.type) {
        case type:
            retVal = 2;
            break;
        case TYPES.EMPTY:
            retVal = 1;
            break;
        default:
            retVal = -1;
            break;
    }
    return retVal;
}

function randomCorner() {
    var x = (Math.random() > 0.5) ? 0 : 2;
    var y = (Math.random() > 0.5) ? 0 : 2;
    return BOARD.grid[x][y];
}