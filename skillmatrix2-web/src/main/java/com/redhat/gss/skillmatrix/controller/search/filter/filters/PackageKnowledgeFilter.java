package com.redhat.gss.skillmatrix.controller.search.filter.filters;

import com.redhat.gss.skillmatrix.controller.search.filter.Filter;
import com.redhat.gss.skillmatrix.controller.search.filter.FilterType;
import com.redhat.gss.skillmatrix.controller.search.filter.MemberFilter;
import com.redhat.gss.skillmatrix.controller.search.filter.exeptions.TypeMismatchException;
import com.redhat.gss.skillmatrix.controller.search.filter.filters.util.AttributeEncoder;
import com.redhat.gss.skillmatrix.controllers.sorthelpers.MemberModelHelper;
import com.redhat.gss.skillmatrix.data.dao.exceptions.PackageInvalidException;
import com.redhat.gss.skillmatrix.data.dao.interfaces.PackageDAO;
import com.redhat.gss.skillmatrix.data.dao.producers.interfaces.MemberProducer;
import com.redhat.gss.skillmatrix.data.dao.producers.util.OperatorEnum;
import com.redhat.gss.skillmatrix.model.Package;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.val;

/**
 * Created with IntelliJ IDEA.
 * User: jtrantin
 * Date: 2/4/14
 * Time: 8:32 AM
 * To change this template use File | Settings | File Templates.
 */
@MemberFilter(id ="pkgKnow",
        type = FilterType.ADVANCED,
        page = "pkgKnow.xhtml",
        name = "knowledge of tags")
@ToString(includeFieldNames=true, exclude={"log", "pkgDao"})
@EqualsAndHashCode(of="packagesMap")
public class PackageKnowledgeFilter implements Filter {
    private Logger log = Logger.getLogger(getClass().getName());

    @Getter
    private Map<Package, Integer> packagesMap;

    public PackageKnowledgeFilter() {
        packagesMap = new HashMap<Package, Integer>();
        log.info(getClass().getSimpleName() + " filter created");
    }

    @Override
    public String encode() {
       val data = new HashMap<String, String>(packagesMap.size());
        for (val entry : packagesMap.entrySet()) {
            String key = "pkg" + entry.getKey().getId();
            String value = entry.getValue().toString();
            data.put(key, value); //put 'pkgXX'-> Y, where XX is pkg ID, Y is min. know level
        }

        return AttributeEncoder.encodeFromMap("pkgKnow", data);
    }

    @Override
    public void decode(@NonNull String filter) throws TypeMismatchException, IllegalArgumentException {
        val data = AttributeEncoder.decodeToMap(filter, "pkgKnow");
        Pattern pkgPattern = Pattern.compile("^pkg([0-9]+)$");
        for (val entry : data.entrySet()) {
            Matcher keyMatcher = pkgPattern.matcher(entry.getKey());
            if (keyMatcher.matches() && entry.getValue().matches("^(0|1|2)$")) { //key and value match, this is valid parameter
              long id = Long.parseLong(keyMatcher.group(1));
                Package pkg = loadPackage(id);
                if(pkg!=null)
                    packagesMap.put(pkg, Integer.parseInt(entry.getValue()));
            }
        }

    }

    private PackageDAO pkgDao;
    private Package loadPackage(long id) {
        if (pkgDao == null) {
            InitialContext initialContext = null;
            try { //acquire BeanManager in order to get SbrDao
                initialContext = new InitialContext();
                val beanManager =  (BeanManager) initialContext.lookup("java:comp/BeanManager");

                val bean = (Bean<PackageDAO>)beanManager.getBeans(PackageDAO.class).iterator().next();
                CreationalContext<PackageDAO> context = beanManager.createCreationalContext(bean);
                pkgDao = (PackageDAO)beanManager.getReference(bean, PackageDAO.class, context);
            } catch (NamingException e) {
                log.severe(String.format("Cannot create/inject PackageDAO. %s\n%s", e.toString(), Arrays.toString(e.getStackTrace())));
            }
        }


        val pkgs = pkgDao.getProducerFactory ().filterId(id).getPackages();
        if(!pkgs.isEmpty())
            return pkgs.get(0);

        return null;
    }

    @Override
    public boolean apply(MemberModelHelper modelHelper) {
        return false;  // cannot be applied to a model
    }

    @Override
    public void applyOnProducer(@NonNull MemberProducer producer) {
        for (val entry : packagesMap.entrySet()) {
            try {
            producer.filterKnowledgeOfPackage(entry.getKey(), entry.getValue(), OperatorEnum.BIGGER_OR_EQUAL);
            } catch (PackageInvalidException e) { //log and ignore
                log.warning(String.format("Invalid package. %s\n%s", e.toString(), Arrays.toString(e.getStackTrace())));
            }
        }
    }

    @Override
    public String explain() {
        StringBuilder desc = new StringBuilder("knowledge of packages: <ul>");
        for (val entry : packagesMap.entrySet()) {
            desc.append("<li>");
            desc.append(entry.getKey().getName());

            switch (entry.getValue()) {
                case 0:
                    desc.append(" at least beginner knowledge"); break;
                case 1: desc.append(" at least intermediate knowledge"); break;
                case 2: desc.append(" expert knowledge"); break;
            }

            desc.append("</li>");
        }
        desc.append("</ul>");

        return desc.toString();
    }

    public List<Package> getPackages() {
        return new ArrayList<Package>(packagesMap.keySet());
    }

    public void addPackage(Package pkg) {
        if(pkg==null)
            return;
        packagesMap.put(pkg, 0);
        log.log(Level.INFO, "{0} pkg added", pkg.getName());
    }

    public void removePackage(Package pkg) {
        packagesMap.remove(pkg);
    }

    public boolean isPackageUsed(Package pkg) {
        return packagesMap.containsKey(pkg);
    }
}
