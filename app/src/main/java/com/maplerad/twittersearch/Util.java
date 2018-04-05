package com.maplerad.twittersearch;

/**
 * Created by Polygod on 4/5/18.
 */

/*
* Provides utility functions. Also known as, no clue where else to put these.
*/
public class Util {
    public static void print(String s) {
        System.out.println(s);
    }

    public static class Pair<L,R> {
        private L l;
        private R r;
        public Pair(L l, R r){
            this.l = l;
            this.r = r;
        }
        public L getL(){ return l; }
        public R getR(){ return r; }
        public void setL(L l){ this.l = l; }
        public void setR(R r){ this.r = r; }
    }

}
