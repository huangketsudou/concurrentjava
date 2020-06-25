package com.jiedong.threadpools;

import java.util.Set;

/**
 * @author 19411
 * @date 2020/06/25 23:24
 **/
public interface Puzzle <P, M> {
    P initialPosition();

    boolean isGoal(P position);

    Set<M> legalMoves(P position);

    P move(P position, M move);
}
