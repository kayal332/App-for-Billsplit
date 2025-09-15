public class person {
	
		private String name;
		private double amountpaid;
		private double balance;
		public person(String name){
			this.name=name;
			this.amountpaid=0.0;
			this.balance=0.0;
			}
			public String getName(){
				return name;
			}
			public double getAmountpaid(){
				return amountpaid;
			}
			public double getBalance(){
				return balance;
			}
			public void addExpense(double amt){
				if(amt<0) throw new IllegalArgumentException("Amount cannot be negative");
				this.amountpaid+=amt;
			}
			 public void setBalance(double balance) { 
				 this.balance = balance;
				 }

	    @Override
	    public String toString() {
	        return String.format("%s -> Paid: %.2f, Balance: %.2f", name, amountpaid, balance);
	    }
	}

