file1 = input("Give the full path to the zpl file")
file2 = input("Give the name for the new text file")
file = open(file1, 'r')
x = file.readlines()
file2 = open(file2, 'w')
file2.write('')
file2.close()
finalfile = open(file2 , 'a')
for i in range(len(x)):
    if (x[i].find('C:')!=-1):
       b = x[i].find('3')
       c = x[i][x[i].find('C:'):b+1]
       c = c + "\n"
       finalfile.write(c)

file.close()
finalfile.close()
