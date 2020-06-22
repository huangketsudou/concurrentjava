package com.jiedong.BuildingBlocks;

import com.jiedong.annotations.ThreadSafe;

import javax.servlet.*;
import java.math.BigInteger;

/**
 * @author 19411
 * @date 2020/06/22 17:19
 **/
@ThreadSafe
public class Factorizer extends GenericServlet implements Servlet {
    private final Computable<BigInteger, BigInteger[]> c =
            new Computable<BigInteger, BigInteger[]>() {
                public BigInteger[] compute(BigInteger arg) {
                    return factor(arg);
                }
            };
    private final Computable<BigInteger, BigInteger[]> cache
            = new Memoizer<BigInteger, BigInteger[]>(c);

    @Override
    public void service(ServletRequest req,
                        ServletResponse resp) {
        try {
            BigInteger i = extractFromRequest(req);
            encodeIntoResponse(resp, cache.compute(i));
        } catch (InterruptedException e) {
            encodeError(resp, "factorization interrupted");
        }
    }

    void encodeIntoResponse(ServletResponse resp, BigInteger[] factors) {
    }

    void encodeError(ServletResponse resp, String errorString) {
    }

    BigInteger extractFromRequest(ServletRequest req) {
        return new BigInteger("7");
    }

    BigInteger[] factor(BigInteger i) {
        // Doesn't really factor
        return new BigInteger[]{i};
    }
}
