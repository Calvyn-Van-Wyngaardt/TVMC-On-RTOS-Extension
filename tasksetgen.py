import random as r

r.seed(1234)

# Trying to make the taskset schedulable...
mult = 450

for i in range(0, 10):
    pUpper = 100000
    pLower = 99999
    
    for tasksetSize in range (2, 25):
        pUpper += (i+1) * mult
        pLower -= (i+1) * mult
        if (pLower <= 0):
            pLower *= -1
        
        currTasks = []    
        # j = 0
        
        for task in range(0, tasksetSize):
            period = r.randint(pLower, pUpper)
            wcet = r.randint(1, period)
            # deadline = r.randint(wcet, pUpper)    
            deadline = r.randint(wcet, (wcet + r.randint(0, mult)))
            print("New Task:")
            print(f"t{tasksetSize}-{i}: {wcet} {period} {deadline}")
            currTasks.append([wcet, period, deadline])
#            j += 1
        fileName = f"0{tasksetSize}{i}"
        if (tasksetSize > 9):
            fileName = f"{tasksetSize}{i}"            
        with open("output/taskSet-" + f"{fileName}.txt", 'w') as output_file:
            [ output_file.write(f"t{j} {currTasks[j][0]} {currTasks[j][1]} {currTasks[j][2]}\n") for j in range(0, tasksetSize) ]
        