package com.niz.actions;

public class ABuildWall extends BuildAction {
	int x;
	@Override
	public void update(float dt) {
		
		if (changed){
			int diff = currentX - x;
			if (
					diff == -1 
					|| 
					diff == 1
					){
				setSolid(x, currentY);
			}
		}
	}

}
