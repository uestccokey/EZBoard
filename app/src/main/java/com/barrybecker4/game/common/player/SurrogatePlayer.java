///** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
//package com.barrybecker4.game.common.player;
//
//import com.barrybecker4.game.common.GameContext;
//import com.barrybecker4.game.common.online.GameCommand;
//import com.barrybecker4.game.common.online.OnlineChangeListener;
//import com.barrybecker4.game.common.online.server.IServerConnection;
//
///**
// * On the server, all players are surrogates except for the robot players.
// * On the client, all players are surrogates except for the human player that is controlling that client.
// *
// * @author Barry Becker
// */
//public class SurrogatePlayer extends Player implements OnlineChangeListener {
//
//    private Player player_;
//
//
//    /**
//     * @param player player to act as surrogate for
//     * @param connection to the server so we can get updated actions.
//     */
//    public SurrogatePlayer(Player player, IServerConnection connection) {
//        super(player.getName(), player.getColor(), player.isHuman());
//        player_ = player;
//        connection.addOnlineChangeListener(this);
//    }
//
//    @Override
//    public synchronized boolean handleServerUpdate(GameCommand cmd) {
//
//        if (cmd.getName() == GameCommand.Name.DO_ACTION) {
//            PlayerAction action = (PlayerAction) cmd.getArgument();
//            GameContext.log(0, "in SurrogatePlayer handleServerUpdate (currently ignored) action =" + action);
//            /// @@ need to do something for regular players here.
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * The player that we are representing (that is actually located somewhere else)
//     * @return the specific game player backed by another player of the same type somewhere else.
//     */
//    @Override
//    public Player getActualPlayer() {
//        return player_;
//    }
//
//
//    @Override
//    public boolean isSurrogate() {
//        return true;
//    }
//}