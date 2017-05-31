package gestionReportes;

import otros.DataAccesObject;

public class DAOReportes extends DataAccesObject {
	private static DAOReportes instance = null;
      
	private DAOReportes(){
		super();
	}

	public static DAOReportes getInstance() {
		if(DAOReportes.instance == null) {
			DAOReportes.instance = new DAOReportes();
		}
		return DAOReportes.instance;
	}
}