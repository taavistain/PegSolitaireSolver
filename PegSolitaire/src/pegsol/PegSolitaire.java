/**
 *  The MIT License (MIT)
 *
 * Copyright (c) 2015 taavistain
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package pegsol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author taavistain
 * @version 5.4.2015
 * @see <a href="http://fi.wikipedia.org/wiki/Lautapasianssi">http://fi.wikipedia.org/wiki/Lautapasianssi</a>
 */
public class PegSolitaire {
    private static final int E = 0;
    private static final int N = 1;
    private static final int T = 2;
    
    private static int curPerm = 0;
    private static int[][] permutations = new int[8][4];
    private static long start;
    
    private int max;
    private int[][] lauta;
    private int napit = Integer.MAX_VALUE;
    private Siirto[] ratk;
    
    private void init() {
        lauta = new int[7][];
        lauta[0] = new int[] { E, E, N, N, N, E, E };
        lauta[1] = new int[] { E, E, N, N, N, E, E };
        lauta[2] = new int[] { N, N, N, N, N, N, N };
        lauta[3] = new int[] { N, N, N, T, N, N, N };
        lauta[4] = new int[] { N, N, N, N, N, N, N };
        lauta[5] = new int[] { E, E, N, N, N, E, E };
        lauta[6] = new int[] { E, E, N, N, N, E, E };
        
        napit = Integer.MAX_VALUE;
        ratk = null;
        
        max = laskeNapit(lauta);
    }
    
    void print() {
        print(lauta);
    }
    
    private void print(int[][] lauta) {
        for(int i = 0; i < lauta.length; i++) {
            for(int j = 0; j < lauta[i].length; j++) {
                System.out.print(c(lauta[i][j]));
                System.out.print(' ');
            }
            System.out.println();
        }
    }
    
    private char c(int a) {
        switch(a) {
            case E:
                return ' ';
            case N:
                return '.';
            case T:
                return 'o';
            default:
                return '#';
        }
    }
    
    private Siirto[] solve() {
        List<Siirto> siirrot = new ArrayList<>();
        recursionSolve(siirrot, lauta);
        return ratk;
    }
    
    private void recursionSolve(List<Siirto> siirrot, int[][] lauta) {
        if(napit == 1)
            return;
        
        List<Siirto> mahd = mahdSiirrot(lauta);
        int n = laskeNapit(lauta);
        if(mahd.size() == 0) {
            if(n < napit) {
                System.out.println("Mahdollinen Ratkaisu ajassa: "+(System.currentTimeMillis()-start)+" ms");
                System.out.println("Nappeja: "+n);
                System.out.println("Siirtoja: "+siirrot.size());
                System.out.println("----------------------------------------------------------------------");
                napit = n;
                if(n <= 4)
                    ratk = siirrot.toArray(new Siirto[] {}); //Löytyi ratkasu
            }
            return;
        }
        if(siirrot.size() >= max) //Liian paljon :P
            return;
        
        for(Siirto s : mahd) {
            siirrot.add(s);
            s.siirry(lauta);
            recursionSolve(siirrot, lauta);
            s.reverse(lauta);
            siirrot.remove(s);
        }
    }
    
    private List<Siirto> mahdSiirrot(int[][] lauta) {
        List<Siirto> mahd = new ArrayList<>();
        for(int y = 0; y < lauta.length; y++) {
            for(int x = 0; x < lauta[y].length; x++) {
                if(lauta[y][x] == PegSolitaire.E || lauta[y][x] == T) 
                    continue;
                Siirto[] t = get(lauta, x, y);
                for(Siirto st : t)
                    if(st != null)
                        mahd.add(st);
            }
        }
        return mahd;
    }
    
    Siirto[] get(int[][] lauta, int x, int y) {
        Siirto[] ss = new Siirto[4];
        int[] p = permutations[curPerm];
        if(ok(lauta, x, y, 0, 1)) //Alas
            ss[p[0]] = new Siirto(x, y,
                                  x, y+1,
                                  x, y+2);
        
        if(ok(lauta, x, y, 0, -1)) //Ylös
            ss[p[1]] = new Siirto(x, y,
                                  x, y-1,
                                  x, y-2);
        
        if(ok(lauta, x, y, 1, 0)) //Oikee
            ss[p[2]] = new Siirto(x,   y,
                                  x+1, y,
                                  x+2, y);
        
        if(ok(lauta, x, y, -1, 0)) //Vasen
            ss[p[3]] = new Siirto(x,   y,
                                  x-1, y,
                                  x-2, y);
        return ss;
    }
    
    private int laskeNapit(int[][] lauta) {
        int sum = 0;
        for(int i = 0; i < lauta.length; i++) {
            for(int j = 0; j < lauta[i].length; j++) {
                if(lauta[i][j] == N)
                    sum++;
            }
        }
        return sum;
    }
    
    private boolean ok(int[][] lauta, int x, int y, int dx, int dy) {
        int ny = y + dy, nx = x + dx;
        if(ny < 0 || ny >= lauta.length || nx < 0 || nx >= lauta[y].length)
            return false;
        if(lauta[ny][nx] != N )
            return false;
        ny += dy; nx += dx;
        if(ny < 0 || ny >= lauta.length || nx < 0 || nx >= lauta[y].length)
            return false;
        if(lauta[ny][nx] != T)
            return false;
        return true;
    }
    
    /**
     * @param args arguments
     */
    public static void main(String[] args) {
        makePerm();
        PegSolitaire sol = new PegSolitaire();
        long end;
        curPerm = 7;
        sol.init();
        start = System.currentTimeMillis();
        Siirto[] s = sol.solve();
        end = System.currentTimeMillis() - start;
        System.out.println("Valmis ajassa: " + end + " ms");
        System.out.println("---------------");
        sol.print();
        System.out.println("---------------");
        for(Siirto si : s)
            si.siirry(sol.lauta);
        sol.print();
        System.out.println("Parhaimman Ratkaisun Siirrot:");
        Arrays.stream(s).forEach(System.out::println);
    }
    
    private static void makePerm() {
        int i = 0;
        permutations[i++] = new int[] {0, 1, 2, 3};
        permutations[i++] = new int[] {1, 2, 3, 0};
        permutations[i++] = new int[] {2, 3, 0, 1};
        permutations[i++] = new int[] {3, 0, 1, 2};
        
        permutations[i++] = new int[] {1, 0, 2, 3};
        permutations[i++] = new int[] {0, 2, 1, 3};
        permutations[i++] = new int[] {0, 1, 3, 2};
        permutations[i++] = new int[] {3, 1, 2, 0};
    }

    private static class Siirto {
        private int fX,fY, mX, mY, tX, tY;
        
        Siirto(int fX, int fY, int mX, int mY, int tX, int tY) {
            this.fX = fX; this.fY = fY; 
            this.tX = tX; this.tY = tY;
            this.mX = mX; this.mY = mY;
        }
        
        @Override
        public String toString() {
            return "("+fX+","+fY+")->("+tX+","+tY+")";
        }
        
        void siirry(int[][] lauta) {
            lauta[tY][tX] = N;
            lauta[mY][mX] = T;
            lauta[fY][fX] = T;
        }
        
        void reverse(int[][] lauta) {
            lauta[tY][tX] = T;
            lauta[mY][mX] = N;
            lauta[fY][fX] = N;
        }
    }
}