package vmdv.control;

import java.util.LinkedList;
import java.util.Set;

import vmdv.model.AbstractGraph;
import vmdv.model.AbstractNode;
import vmdv.model.XYZ;

public class ForceAtlas2Layout extends GraphLayout {
	private float ka = 5f;
	private float kr = 1f;

	@Override
	public void updateLayout(AbstractGraph graph) {
		if (graph.getStart() == null) {
			return;
		}
		// resistance force
		Set<AbstractNode> tns = graph.getNodes();
		LinkedList<AbstractNode> tmp_tns = new LinkedList<AbstractNode>();
		// synchronized(tns) {
		for (AbstractNode tn : tns) {
			tmp_tns.addFirst(tn);
		}
		// }
		for (AbstractNode sn : tns) {
			for (AbstractNode n : tns) {
				if (!sn.id.equals(n.id)) {
					XYZ snp = sn.xyz;
					XYZ np = n.xyz;
					double d2 = Math.pow(snp.getX() - np.getX(), 2) + Math.pow(snp.getY() - np.getY(), 2)
							+ Math.pow(snp.getZ() - np.getZ(), 2);
					// System.out.println("distance: "+d2);
					if (d2 == 0) {
						d2 = 1;
					}
					// XYZ preForce = sn.getForce();
					// sn.setForce(new
					// XYZ(preForce.getX()+(kr*(snp.getX()-np.getX())*Math.pow(d2,
					// -1.5)),
					// preForce.getY()+(kr*(snp.getY()-np.getY())*Math.pow(d2,
					// -1.5)),preForce.getZ()+(kr*(snp.getZ()-np.getZ())*Math.pow(d2,
					// -1.5))));
					double dx = snp.getX() - np.getX();
					double dy = snp.getY() - np.getY();
					double dz = snp.getZ() - np.getZ();
					dx = dx == 0 ? 1 : dx;
					dy = dy == 0 ? 1 : dy;
					dz = dz == 0 ? 1 : dz;
					sn.addForce(kr * (dx) * Math.pow(d2, -1.5), kr * (dy) * Math.pow(d2, -1.5),
							kr * (dz) * Math.pow(d2, -1.5));
				}
			}
		}
		// attraction force
		for (AbstractNode sn : tmp_tns) {
			for (AbstractNode dn: graph.getSuccessors(sn)) {
//				Node dn = te.getTo();
				XYZ snp = sn.xyz;
				XYZ dnp = dn.xyz;
				// double d2 = Math.pow(snp.getX()-np.getX(),
				// 2)+Math.pow(snp.getY()-np.getY(),
				// 2)+Math.pow(snp.getZ()-np.getZ(), 2);
				// if(d2 == 0) {
				// d2 = 1;
				// }
				sn.addForce(ka * (dnp.getX() - snp.getX()), ka * (dnp.getY() - snp.getY()),
						ka * (dnp.getZ() - snp.getZ()));
				dn.addForce(ka * (snp.getX() - dnp.getX()), ka * (snp.getY() - dnp.getY()),
						ka * (snp.getZ() - dnp.getZ()));
			}
		}

		// set move
		for (AbstractNode sn : tmp_tns) {
			XYZ force = sn.force;
			XYZ p = sn.xyz;
			sn.setXYZ(p.getX() + force.getX() * 0.02, p.getY() + force.getY() * 0.02, p.getZ() + force.getZ() * 0.02);
			// double xyz = Math.sqrt(Math.pow(sn.getX(), 2)+Math.pow(sn.getY(),
			// 2)+Math.pow(sn.getY(), 2));
			// double d = sn.getDepth();
			// sn.setXYZ(sn.getX()*d/xyz, sn.getY()*d/xyz, sn.getZ()*d/xyz);
			// sn.setLabel(" "+d);
			sn.setForce(0, 0, 0);
			// force = sn.getForce();
			// System.out.println("Node force: "+force.getX()+",
			// "+force.getY()+","+force.getZ());
			// System.out.println("Node Position: "+(p.getX()+force.getX())+",
			// "+(p.getY()+force.getY())+","+(p.getZ()+force.getZ()));
		}
		graph.getStart().setXYZ(0, 0, 0);
	}

}
