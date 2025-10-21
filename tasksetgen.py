import random as r

r.seed(1234)

# Trying to make the taskset schedulable...
mult = 100

for i in range(0, 10):
    pUpper = 10000
    pLower = 9999
    
    for tasksetSize in range (2, 10):
        pUpper += i * mult
        pLower -= i * mult
        currTasks = []    
        
        for task in range(0, tasksetSize):
            period = r.randint(pLower, pUpper)
            wcet = r.randint(1, period)
            deadline = r.randint(wcet, 100000)    
            print("New Task:")
            print(f"t{tasksetSize}-{i}: {wcet} {period} {deadline}")
            currTasks.append([wcet, period, deadline])
            with open("output/taskSet-" + f"{i}-{tasksetSize}.txt", 'w') as output_file:
                [ output_file.write(f"t{task} {t[0]} {t[1]} {t[2]}\n") for t in currTasks]
        