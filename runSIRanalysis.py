#!/usr/bin/python -tt
# -*- coding: utf-8 -*-
# =============================================================================
# File      : runSIRanalysis.py 
# Creation  : 13 Dec 2017
# Time-stamp: <Mit 2017-12-13 12:32 juergen>
#
# Copyright (c) 2017 Jürgen Hackl <hackl@ibi.baug.ethz.ch>
#               http://www.ibi.ethz.ch
# $Id$ 
#
# Description : Master file to run multiple SIR simulations
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>. 
# =============================================================================
import os
import numpy as np
import timeit
from joblib import Parallel, delayed

def run():
    number_of_simulations = [1]
    percentage_infected_persons = [0.0002,0.0004,0.0006,0.0008,0.001]
    recover_probabilities = range(5,105,5)
    infection_probabilities = range(5,105,5)

    # args = []
    # i = 0
    # for l in number_of_simulations:
    #     for m in percentage_infected_persons:
    #         for n in infection_probabilities:
    #             for o in recover_probabilities:
    #                 args.append([i,l,m,n/100,o/100])
    #                 i += 1

    # args=[[1,4,0.0002,0.5,0.2],[2,3,0.004,0.6,0.1]]

    args = []
    for i in range(100):
        args.append([i,i,0.0005,.75,.40])

    #print(args)
    Parallel(n_jobs=35)(delayed(parallel_model)(arg) for arg in args)
    pass

def parallel_model(args):
    print('run number '+str(args[0]))
    args.pop(0)
    os.system('java -cp "temp/source/*:temp/source/libs/*" ch.ethz.ivt.abmt.project.RunSIRAnalysis '+' '.join(str(e) for e in args))
    pass



#if __name__ == '__main__':
def main():
    print('***** start *****')
    start = timeit.default_timer()
    run()
    stop = timeit.default_timer()
    print('time: ' + str(stop - start))
    print('****** end ******')

main()
    
# =============================================================================
# eof
#
# Local Variables: 
# mode: python
# mode: linum
# End: 

 
