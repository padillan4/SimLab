
public class SimLab {

	static int MAX_LIST = 25;
	static int MAX_ATTR = 25;
	static int     list_rank, list_size, next_event_type, maxatr = 0, maxlist = 0;
	static double  transfer, sim_time, prob_distrib[];
	
	public static void main(String[] args) {
		
		init();
		
		
	}
	
	static void init(){
		
		int list, listsize;
		
		if(maxlist < 1){
			maxlist = MAX_LIST;
		}
		listsize = maxlist + 1;
		// initialize system attributes
		sim_time = 0.0;
		if(maxatr < 4){
			maxatr = MAX_ATTR;
		}
		
		
	}

}
