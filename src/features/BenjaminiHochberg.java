/*  
 * $Id$
 * $URL$
 * This file is part of the program TFpredict. TFpredict performs the
 * identification and structural characterization of transcription factors.
 *  
 * Copyright (C) 2010-2014 Center for Bioinformatics Tuebingen (ZBIT),
 * University of Tuebingen by Johannes Eichner, Florian Topf, Andreas Draeger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package features;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

/**
 * this is a rip of the statistic function of MayDay
 * original implemented by "Florian Battke, Roland Keller"
 * 
 * @author Florian Topf
 * @version $Rev$
 * @since 1.0
 */
public class BenjaminiHochberg {

	public ArrayList<Double> correct(Collection<Double> pvalues) {
		int size = pvalues.size();
		ArrayList<Double> ret = new ArrayList<Double>();

		//rank p-values, the highest value gets the 1st rank
		List<Double> lpvalues;
		if (!(pvalues instanceof List))
			lpvalues = new ArrayList<Double>(pvalues);
		else
			lpvalues = (List<Double>)pvalues;
		
		List<Integer> ranks = rank(lpvalues);
		
		HashMap<Integer,Integer> rankMap = new HashMap<Integer,Integer>();
		int position = 0;
		for(int rank:ranks) {
			rankMap.put(rank, position);
			position++;
		}
		
		int j=0;
		for (double p : pvalues) {
			double pc = (p*(double)size/(double)ranks.get(j));
			if (pc>1.0) 
				pc=1.0;
			ret.add(pc);
			++j;
		}
		
		for(int rank=size-1;rank!=0;rank--) {
			int currentIndex = rankMap.get(rank);
			int nextIndex = rankMap.get(rank+1);
			double pValue=Math.min(ret.get(currentIndex),ret.get(nextIndex));
			ret.set(currentIndex,pValue);
		}
		return ret;
	}
	
	// taken from mayday
	public static List<Integer> rank(List<Double> x)
	{
		List<Integer> res=new ArrayList<Integer>(x.size());
		for (int i=0; i!=x.size(); ++i)
			res.add(0);
		
		int i=0;

		for (int r:createSortingSet(x)) {
			res.set(r,++i);
		}
		return res;
	}
	
	
	// taken from mayday
	protected static TreeSet<Integer> createSortingSet(List<Double> x) {
		DoubleListComparator dbArrComp = new DoubleListComparator(x);
		TreeSet<Integer> SortingIndices = new TreeSet<Integer>(dbArrComp);
		
		for (int i = 0; i < x.size(); i++) 
			SortingIndices.add(i);
		
		return SortingIndices;
	}
	
	
	// taken from mayday
	public static final class DoubleListComparator implements Comparator<Integer>
	{
		List<Double> compDoubList;
		
		public DoubleListComparator(List<Double> doubleList) {
			this.compDoubList = doubleList;
		}
		
		public int compare(Integer ind1, Integer ind2) 
		{
			// Equal numbers are treated as different to keep all numbers alive in TreeSet.
			// If this would not be done TreeSet would reduce ties to only one number.
			// ==> NEVER RETURN 0
			
			double d1 = compDoubList.get(ind1);
			double d2 = compDoubList.get(ind2);
			
			if (Double.isNaN(d1))
				return 1; 
			if (Double.isNaN(d2))
				return -1;
			
			if (d1-d2 < 0d) 
				return -1;
			
			return 1;
		}
	}

	
}
