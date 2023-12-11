# LatticePath
Console Java program to print in ASCII all the different paths from the 0,0 corner to the opposite corner in a grid of points.
The option _--pass_ allows to surpass the diagonal and the option _--diag_ allows to use diagonal steps.
The option _--count_ prints a table with the number of all the different paths to each point of the grid.

    > java -jar LatticePath.jar 
    Error: Parameters: java LatticePath [--pass] [--diag] [--count] ROWS [COLUMNS]
    > java -jar LatticePath.jar 2 3
    o---o---o
            |
            o
    
    o---o
        |
        o---o
    
    > java -jar LatticePath.jar --pass 2 3
    o---o---o
            |
            o
    
    o---o
        |
        o---o
    
    o
    |
    o---o---o
    
    java -jar LatticePath.jar --diag 2 3
    o---o---o
            |
            o
    
    o---o
         '.
           'o
    
    o---o
        |
        o---o
    
    o
     '.
       'o---o
    
    > java -jar LatticePath.jar --pass --diag 2 3
    o---o---o
            |
            o
    
    o---o
         '.
           'o
    
    o---o
        |
        o---o
    
    o
     '.
       'o---o
    
    o
    |
    o---o---o

    > java -jar LatticePath.jar --pass --diag --count 7
    1        1        1        1        1        1        1
    1        3        5        7        9       11       13
    1        5       13       25       41       61       85
    1        7       25       63      129      231      377
    1        9       41      129      321      681     1289
    1       11       61      231      681     1683     3653
    1       13       85      377     1289     3653     8989

The following links offer more information about each type:
* java LatticePath --nopass --nodiag M N
  https://en.wikipedia.org/wiki/Catalan's_triangle and https://oeis.org/A009766
* java LatticePath --pass --nodiag M N
  https://en.wikipedia.org/wiki/Pascal%27s_triangle and https://oeis.org/A007318
* java LatticePath --nopass --diag M N
  https://en.wikipedia.org/wiki/Schr%C3%B6der_number and https://oeis.org/A033877
* java LatticePath --pass --diag M N
  https://en.wikipedia.org/wiki/Delannoy_number and https://oeis.org/A008288
