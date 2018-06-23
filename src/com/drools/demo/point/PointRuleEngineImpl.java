package com.drools.demo.point;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.spi.Activation;

/**
 * ����ӿ�ʵ����
 * @author quzishen
 */
public class PointRuleEngineImpl implements PointRuleEngine {
	private RuleBase ruleBase;

	/* (non-Javadoc)
	 * @see com.drools.demo.point.PointRuleEngine#initEngine()
	 */
	public void initEngine() {
		// ����ʱ���ʽ
		System.setProperty("drools.dateformat", "yyyy-MM-dd HH:mm:ss");
		ruleBase = RuleBaseFacatory.getRuleBase();
		try {
			PackageBuilder backageBuilder = getPackageBuilderFromDrlFile();
			ruleBase.addPackages(backageBuilder.getPackages());
		} catch (DroolsParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.drools.demo.point.PointRuleEngine#refreshEnginRule()
	 */
	public void refreshEnginRule() {
		ruleBase = RuleBaseFacatory.getRuleBase();
		org.drools.rule.Package[] packages = ruleBase.getPackages();
		for(org.drools.rule.Package pg : packages) {
			ruleBase.removePackage(pg.getName());
		}
		
		initEngine();
	}

	/* (non-Javadoc)
	 * @see com.drools.demo.point.PointRuleEngine#executeRuleEngine(com.drools.demo.point.PointDomain)
	 */
	public void executeRuleEngine(final PointDomain pointDomain) {
		if(null == ruleBase.getPackages() || 0 == ruleBase.getPackages().length) {
			return;
		}
		
		StatefulSession statefulSession = ruleBase.newStatefulSession();
		statefulSession.insert(pointDomain);
		
		// fire
		statefulSession.fireAllRules(new org.drools.spi.AgendaFilter() {
			public boolean accept(Activation activation) {
				return !activation.getRule().getName().contains("_test");
			}
		});
		
		statefulSession.dispose();
	}

	/**
	 * ��Drl�����ļ��ж�ȡ����
	 * @return
	 * @throws Exception
	 */
	private PackageBuilder getPackageBuilderFromDrlFile() throws Exception {
		// ��ȡ���Խű��ļ�
		List<String> drlFilePath = getTestDrlFile();
		// װ�ز��Խű��ļ�
		List<Reader> readers = readRuleFromDrlFile(drlFilePath);

		PackageBuilder backageBuilder = new PackageBuilder();
		for (Reader r : readers) {
			backageBuilder.addPackageFromDrl(r);
		}
		
		// ���ű��Ƿ�������
		if(backageBuilder.hasErrors()) {
			throw new Exception(backageBuilder.getErrors().toString());
		}
		
		return backageBuilder;
	}

	/**
	 * @param drlFilePath �ű��ļ�·��
	 * @return
	 * @throws FileNotFoundException
	 */
	private List<Reader> readRuleFromDrlFile(List<String> drlFilePath) throws FileNotFoundException {
		if (null == drlFilePath || 0 == drlFilePath.size()) {
			return null;
		}

		List<Reader> readers = new ArrayList<Reader>();

		for (String ruleFilePath : drlFilePath) {
			readers.add(new FileReader(new File(ruleFilePath)));
		}

		return readers;
	}

	/**
	 * ��ȡ���Թ����ļ�
	 * 
	 * @return
	 */
	private List<String> getTestDrlFile() {
		List<String> drlFilePath = new ArrayList<String>();
		drlFilePath
				.add("D:/workspace2/DroolsDemo/src/com/drools/demo/point/addpoint.drl");
		drlFilePath
				.add("D:/workspace2/DroolsDemo/src/com/drools/demo/point/subpoint.drl");

		return drlFilePath;
	}
}
