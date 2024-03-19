package asciir;

public class Asciir extends Variables
{
    public static void main(String[] args)
    {
        for (char c : ASCII) System.out.println(c);
        for (int i = 0; i < 200; i++) System.out.println((char) i + " " + i);
    }
}

