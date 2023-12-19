import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day19 {
    public static void main(String... args) {
        for (String input : List.of(TEST_INPUT, INPUT)) {
            var sections = input.split("\n\n");
            var workflows = parseWorkflows(sections[0]);
            var partList = parseParts(sections[1]);
            System.out.println("Part I: " + partI(partList, workflows));
            System.out.println("Part II: " + partII(workflows));
        }
    }

    private static long partI(List<Part> partList, Map<String, Workflow> workflows) {
        return partList.stream().filter(p -> p.accepted(workflows)).mapToLong(Part::sum).sum();
    }

    private static long partII(Map<String, Workflow> workflows) {
        return acceptedCombinations(workflows, "in", new Range());
    }

    private static Map<String, Workflow> parseWorkflows(String workflowsSection) {
        return workflowsSection.lines().map(line -> {
            var name = line.substring(0, line.indexOf("{"));

            var rules = Stream.of(line.substring(name.length() + 1, line.length() - 1).split(",")).map(rule -> {
                if (rule.contains(":")) {
                    var cond = rule.substring(0, rule.indexOf(":"));
                    var dest = rule.substring(rule.indexOf(":") + 1);
                    return new Rule(cond, dest);
                } else {
                    return new Rule(rule);
                }
            }).toList();

            return new Workflow(name, rules);
        }).collect(Collectors.toMap(Workflow::name, w -> w));
    }

    private static List<Part> parseParts(String partsSection) {
        return partsSection
                .lines()
                .map(line -> line.substring(1, line.length() - 1))
                .map(line -> Stream.of(line.split(","))
                                   .map(arg -> arg.split("="))
                                   .collect(Collectors.toMap(arg -> arg[0], arg -> Integer.parseInt(arg[1]))))
                .map(Part::new)
                .toList();
    }

    private static long acceptedCombinations(Map<String, Workflow> workflows, String name, Range range) {
        if (name.equals("A")) {
            return range.combinations();
        } else if (name.equals("R")) {
            return 0;
        }

        long sum = 0;

        for (var rule : workflows.get(name).rules()) {
            Range trueRange = rule.trueRange(range);
            sum += acceptedCombinations(workflows, rule.destination(), trueRange);
            range = rule.falseRange(range);
        }

        return sum;
    }

    record Interval(int min, int max) {}

    record Range(Map<String, Interval> variables) {
        Range() {
            this(Map.of(
                    "x", new Interval(1, 4000),
                    "m", new Interval(1, 4000),
                    "a", new Interval(1, 4000),
                    "s", new Interval(1, 4000)));
        }

        Range min(String variable, int min) {
            var nv = new HashMap<>(variables);
            nv.put(variable, new Interval(min, nv.get(variable).max()));
            return new Range(nv);
        }

        Range max(String variable, int max) {
            var nv = new HashMap<>(variables);
            nv.put(variable, new Interval(nv.get(variable).min(), max));
            return new Range(nv);
        }

        public long combinations() {
            return variables.values()
                            .stream()
                            .mapToLong(interval -> interval.max() - interval.min() + 1)
                            .reduce(1, (a, b) -> a * b);
        }
    }

    record Part(Map<String, Integer> values) {
        public long sum() {
            return values.values().stream().mapToLong(l -> l).sum();
        }

        public boolean accepted(Map<String, Workflow> workflows) {
            var workflow = workflows.get("in");

            while (true) {
                for (var rule : workflow.rules()) {
                    if (rule.appliesTo(values)) {
                        if (rule.destination().equals("A")) {
                            return true;
                        } else if (rule.destination().equals("R")) {
                            return false;
                        }

                        workflow = workflows.get(rule.destination());
                        break;
                    }
                }
            }
        }
    }

    record Workflow(String name, List<Rule> rules) {}

    record Rule(String variable, boolean lessThan, int value, String destination) {
        Rule(String destination) {
            this(null, false, 0, destination);
        }

        Rule(String condition, String destination) {
            this(condition.substring(0, operatorPos(condition)),
                    condition.contains("<"),
                    Integer.parseInt(condition.substring(operatorPos(condition) + 1)),
                    destination);
        }

        private static int operatorPos(String condition) {
            return Math.max(condition.indexOf("<"), condition.indexOf(">"));
        }

        public boolean appliesTo(Map<String, Integer> values) {
            if (variable == null) {
                return true;
            }

            return lessThan ? values.get(variable) < value : values.get(variable) > value;
        }

        public Range trueRange(Range range) {
            if (variable == null) {
                return range;
            }

            return lessThan ? range.max(variable, value - 1) : range.min(variable, value + 1);
        }

        public Range falseRange(Range range) {
            if (variable == null) {
                return null;
            }

            return lessThan ? range.min(variable, value) : range.max(variable, value);
        }
    }

    static final String TEST_INPUT = """
            px{a<2006:qkq,m>2090:A,rfg}
            pv{a>1716:R,A}
            lnx{m>1548:A,A}
            rfg{s<537:gd,x>2440:R,A}
            qs{s>3448:A,lnx}
            qkq{x<1416:A,crn}
            crn{x>2662:A,R}
            in{s<1351:px,qqz}
            qqz{s>2770:qs,m<1801:hdj,R}
            gd{a>3333:R,R}
            hdj{m>838:A,pv}
                        
            {x=787,m=2655,a=1222,s=2876}
            {x=1679,m=44,a=2067,s=496}
            {x=2036,m=264,a=79,s=2244}
            {x=2461,m=1339,a=466,s=291}
            {x=2127,m=1623,a=2188,s=1013}""";

    static final String INPUT = """
            gl{x<3407:mnr,pb}
            msm{x>3867:A,R}
            qcn{s>1407:A,s>1324:R,x>3615:R,R}
            bp{s<304:R,x<3585:fr,bhm}
            tdq{x<2920:R,xk}
            qfh{s<3501:bxd,zbz}
            pd{x>3157:qlc,m>2947:cx,m<2822:hph,ffk}
            pj{m>3577:A,R}
            vrr{x<3490:A,A}
            cm{m<2560:R,A}
            qkb{m<2285:fhg,x>3244:lb,kp}
            dvx{x>536:zr,nk}
            zg{a<713:R,s<2175:A,s<2326:R,R}
            pgz{m<714:A,A}
            glp{x<3664:R,x>3786:R,R}
            njr{m>2591:ksg,s<1640:A,m>2062:cr,A}
            ht{x>2398:R,R}
            rk{s<2859:tl,A}
            rzs{a>1034:A,x<1457:A,a<990:R,A}
            qhd{s>683:A,A}
            rj{x<3469:R,R}
            tv{m>402:R,x<1830:A,x<2001:A,A}
            lkz{x<2728:A,m>930:A,A}
            fhh{m>1818:R,m>1402:A,R}
            tk{s<916:A,a>1485:R,s<1059:A,A}
            jz{a<120:R,R}
            lcq{m>731:R,A}
            hm{s<3385:R,R}
            jb{m>1389:jdb,s<472:snz,x>1651:ljv,psn}
            vfq{m<2900:R,m<2913:R,lck}
            rdl{x<764:jg,ptm}
            pmg{x>3227:A,m<1050:R,A}
            gsj{a<907:nxv,m>1532:cff,th}
            mz{s<671:np,a<2135:rp,a>2814:df,gk}
            gh{x<1467:A,A}
            mpx{s>3658:lhh,a<1514:gl,fff}
            fmn{a>1920:R,R}
            dsz{x<2924:pr,x>3396:txt,dzg}
            ml{x<3579:R,R}
            qd{m<3201:R,A}
            cz{m<1108:gkj,m>1462:rmt,bhb}
            jg{a<2880:A,s>2998:A,R}
            mmt{x<569:vvh,s>1530:kdn,a>2558:rr,gsj}
            jsc{x>267:A,x>107:A,x>51:ddg,A}
            cc{s<1193:lg,m>2468:cpm,nbm}
            bhz{a<2658:R,jcr}
            lh{x<3100:A,a>3666:A,dls}
            clt{m<964:R,x>3420:R,A}
            kgd{x<2546:R,m>1765:A,A}
            sj{m<1292:A,m>1713:A,A}
            rxv{x>3807:A,m<2456:R,m<2757:A,R}
            zt{a<2558:A,R}
            gj{s>1526:A,s<1361:rvf,A}
            gr{s<803:qhd,R}
            gbm{m<1928:jt,s>3624:A,glp}
            jhs{m<3643:R,a>2138:R,rqq}
            zr{m>1639:R,x<786:A,R}
            nrb{a<752:R,R}
            rl{s>2322:sj,qp}
            zgr{s<1512:R,x<2426:R,A}
            vql{s>831:R,R}
            ppr{x<1998:R,a>1239:R,x>2062:A,A}
            nb{s<1517:A,s>1694:A,A}
            cjg{x>1567:R,s<417:R,A}
            bxr{s>1301:jv,R}
            sl{m>1570:A,s<2596:A,x<1151:A,A}
            xc{s>3360:cb,x<2803:bm,x<2913:mk,tvg}
            ld{x>3301:zx,xmr}
            msb{m>1184:A,x>2341:A,R}
            cj{s>1524:R,x>2939:A,a>2705:A,R}
            bhb{x<1304:A,s>2596:qqd,A}
            tbf{s<885:A,a<1462:R,a>1660:R,A}
            tkz{m>3241:A,x>3257:A,s>1945:zsq,lfh}
            rc{s>173:R,A}
            hzc{x<3591:A,s<3516:R,m<496:A,A}
            ll{s>2642:A,R}
            gqs{a>2472:A,m<945:crz,s>237:A,pmg}
            csp{x<2808:A,R}
            rg{a>1538:snd,s>332:R,a>1311:R,ppr}
            tfm{s<727:R,R}
            crz{m<863:R,x<3148:R,m>916:A,R}
            dzg{m>1579:td,s>463:gr,m<1169:gqs,sz}
            hv{s<390:R,R}
            hc{x<3387:A,a>870:dps,A}
            kbq{x<3637:pmd,a<2686:nmd,mxp}
            zc{x<1797:ct,bpk}
            qz{x<321:A,x<397:A,R}
            jsx{a>1561:A,s>818:A,R}
            tnd{a>877:R,x>1237:R,hx}
            qbt{s<2039:R,R}
            lf{x<3755:A,s>1430:R,R}
            xnt{x<2789:A,A}
            zl{s>1040:cf,x<3267:kx,ng}
            rh{m<1646:A,R}
            xn{x>2767:A,R}
            zbz{x<3113:lx,a<350:mf,m<1755:hn,px}
            lfh{a>491:R,A}
            gpz{a>3212:A,a>2369:R,R}
            dps{a<1397:A,s>2065:R,x<3776:A,A}
            nq{a<1261:R,A}
            dcj{a<822:A,x>1371:A,A}
            dv{m>3133:jmz,nc}
            dm{s>547:R,a>1610:A,R}
            rss{x>1193:A,x<717:A,R}
            qt{a<818:tvk,s<292:vfj,s>452:sc,cbk}
            fjn{a<2862:qfp,llh}
            fth{s<1568:R,x<213:A,x<432:A,R}
            np{a>1414:bpt,x>3262:R,A}
            zz{s>1387:R,m<1640:R,A}
            vtj{m<1572:A,x>1689:jc,R}
            djl{s>1745:xnd,a<1133:cn,x<3301:R,frm}
            qmc{a>1091:nzt,a<822:nrb,x>3075:R,A}
            qsf{x<2405:vzq,jgd}
            rv{m<2686:nbg,cjz}
            kt{s>1095:A,R}
            fn{x>3754:R,m<795:R,a>699:A,R}
            ncm{a>1877:A,A}
            vhn{x>1536:A,A}
            gvf{s<203:R,x>2596:R,a>1922:R,A}
            hg{x<3754:vrr,hjb}
            xzm{a<198:R,x<2030:R,A}
            pb{a>1368:R,a>1330:A,m<2012:R,R}
            bt{x>3241:R,A}
            mx{a<2078:A,A}
            scm{x>969:R,s>505:A,x<728:R,A}
            psk{m<359:kgp,s<968:qrb,gth}
            bhm{m>387:A,a>2520:R,R}
            mv{x<3594:A,a>1371:dp,m<629:rf,pgz}
            lc{m<2770:A,x>1670:A,a>1746:R,mff}
            lv{s>400:bz,s>134:qvl,A}
            xd{x<1834:R,A}
            snd{s<216:R,x>1996:R,A}
            qvg{m>386:R,x>3668:xcr,pk}
            kb{s<3258:fg,gz}
            dc{m<3018:br,a<1226:R,mb}
            fgd{a<2340:lkz,a>2465:A,s<1247:R,csp}
            mc{a>166:R,R}
            fbd{s<551:A,pj}
            mnr{m>1549:R,R}
            ks{x>560:R,x>301:R,R}
            lck{m<2921:R,a>2471:R,x<3759:A,R}
            vlg{x>3883:R,s>1430:A,R}
            ngs{s>934:kt,m<3213:A,A}
            td{x>3145:A,A}
            mnz{m>1263:R,x>3187:A,a>2429:A,A}
            lnl{x>2705:R,s<1215:qnm,R}
            lz{x<1962:gcn,R}
            hs{x>3807:A,a<2616:R,a>3261:R,R}
            bft{a<3145:R,s>1031:R,s>995:R,A}
            dmb{x>3955:A,s>1477:R,R}
            bg{a<364:A,s>2232:R,A}
            fz{x<2773:A,x<2998:A,a<421:R,R}
            bz{x>1826:A,R}
            bpk{a>2099:R,A}
            nx{m>893:A,m<330:R,R}
            mf{s>3739:nr,jz}
            npf{m<926:fmn,x>1106:km,dn}
            ljl{a<1530:gh,s<279:pdb,a<1810:zd,cjg}
            lb{a<636:A,R}
            vnt{m<2925:A,x<2781:A,m<2973:R,R}
            mk{x<2868:R,s>3225:R,R}
            ptm{a<2314:A,m<2769:A,x>954:R,R}
            clf{s>1339:A,m<3713:R,x<3593:R,R}
            nmd{m>1254:A,x<3784:cnf,A}
            bkp{a<1205:A,x>2839:R,m<2396:R,A}
            cff{m<2815:bvb,m>3559:A,s>1370:R,lrs}
            vfj{s<135:A,rm}
            bsp{s>824:R,m>428:R,A}
            szd{a<684:ck,x<2460:pv,R}
            ck{x<2588:A,R}
            nkr{s<379:zt,m>2637:pd,dmz}
            gk{s<741:R,m<319:R,x<3270:A,mvp}
            kdz{x>2853:R,m>144:A,R}
            gvz{x<3489:R,x<3827:A,s>983:A,R}
            hd{m<528:A,m>845:A,x>720:A,A}
            qx{s>2795:fvp,rl}
            ss{m>2917:A,s>543:R,s<502:R,R}
            rqq{s<1272:R,m<3880:A,s>1278:R,A}
            xk{a<1483:R,m>3533:A,R}
            cpf{s>1148:A,A}
            dj{s>1676:A,crf}
            hph{x>2528:A,x<2400:R,x>2477:A,R}
            in{s>1833:qsf,x<2158:rn,cc}
            jl{m<2093:vzh,pbv}
            bnl{a>1155:jnk,x>3494:gbm,jf}
            km{s<3630:A,m>1218:ljt,x<1608:R,R}
            ckl{m<185:A,x>3910:R,m>367:A,R}
            xf{a>844:mkd,m>1526:nhl,sp}
            zq{a>2088:ngs,s<1018:xhg,kbp}
            zx{s<2982:qr,a<2973:cdc,bpx}
            zmq{m<1448:R,A}
            bfj{m<2520:hc,a>944:dc,s<2038:tkz,rdc}
            rr{m>1337:nh,m<870:ptk,cvk}
            pt{x<2946:A,R}
            pbv{s>758:A,ks}
            hr{s<1601:R,A}
            mkd{m<1705:R,a>1381:R,A}
            fh{m<2402:R,m<3065:vf,x>2819:R,ffs}
            cbk{x>677:R,hv}
            qqd{s>2771:A,x>1994:R,A}
            rpp{a>2894:lcx,a<2748:cj,R}
            jgt{a>672:jrk,a>308:A,mc}
            df{a>3383:tfm,s<738:R,x<3104:A,A}
            cnf{a>2176:R,x>3704:A,s>1564:A,A}
            qsn{x<1179:mmt,cng}
            pfl{x>954:R,m<1288:A,A}
            tc{x>2618:tks,R}
            ksg{s<1616:A,A}
            nbm{a<1856:fs,scq}
            vkk{m<366:A,A}
            frm{m>855:R,s<1693:R,x<3616:A,A}
            kxq{a<2526:R,x>3485:R,s>1333:A,R}
            bh{m>2936:R,a>1449:A,R}
            xbq{s<645:R,R}
            fvp{m>830:A,x<3094:R,A}
            jvr{m>2933:fbq,xnr}
            nc{a<1469:tnd,x>1219:ll,m>2373:rdl,fc}
            gb{s>347:A,R}
            dls{x<3247:A,A}
            kn{x>3438:A,a>998:A,m<717:R,R}
            chx{x>3087:A,m<3577:R,s<1103:R,A}
            nxv{x<794:A,a>364:gf,s>1288:zz,R}
            zp{a>1218:R,s>3680:A,s>3631:R,R}
            ntr{s>1320:A,A}
            sgp{x<978:A,R}
            jcr{a>3386:A,a>3073:A,a<2893:A,R}
            cpm{m>3036:cjm,x<3338:jx,m>2847:jvr,lqk}
            pvl{a<724:R,x>1953:A,A}
            tz{a>634:xd,x>1870:dsq,sxj}
            sb{x>3001:mh,s>1598:szd,kj}
            qp{a>2462:R,A}
            nzt{s>286:R,R}
            pdb{m<881:A,a>1780:R,A}
            sqz{x>3549:A,s<1688:hvg,A}
            nfc{m<457:ml,x<3564:rj,A}
            kdn{x<909:tpd,m<1714:bhz,s>1732:nn,njr}
            jrk{s>1317:A,s<1270:R,R}
            bpt{m>435:A,m>206:A,R}
            tfh{s<1640:A,s>1705:A,s>1664:lml,lcq}
            hbd{x>3358:R,s<965:A,R}
            zlt{a>946:rzs,a<737:R,dcj}
            vb{x<2728:zgr,a<3302:rpp,lh}
            ssv{s>632:R,s>307:A,s>110:A,R}
            sxj{a>287:A,R}
            fb{a>422:pvl,x<1890:R,xzm}
            hxf{m<3316:R,A}
            gkt{a<3703:A,m>2978:A,R}
            hk{s>1516:A,s>1496:A,A}
            qr{x<3544:R,s<2568:A,a<2881:R,R}
            xm{s>1319:A,A}
            kgp{m<239:kdz,m>286:zl,x>3185:bpm,fqn}
            ljv{m>634:rq,fb}
            nbg{m>2610:R,cm}
            dzz{s>1559:shn,s<1387:bxr,a<3020:lmf,crc}
            vf{a>1556:R,m<2767:A,x>2808:A,R}
            xxl{x>3131:vql,jsx}
            nh{x<871:R,a<3183:kmd,A}
            zd{m>960:R,A}
            cmm{a<2668:qcn,a>3436:A,A}
            vzq{m<1709:tvm,dv}
            ddg{s<1584:A,x>83:R,x<71:R,A}
            lsl{s>188:lt,a<231:A,m>284:A,R}
            th{x<969:R,R}
            jmz{m>3688:xrc,x>1120:lz,m>3380:rk,bxh}
            pjv{m<471:dl,ht}
            nqm{s<858:A,x<1390:R,dfl}
            bnb{s<1596:ql,djl}
            nn{x>1014:R,a>1334:sgp,s>1799:A,A}
            pr{a>2081:fjn,a<1158:hj,zm}
            cv{m<1433:pn,a>1436:fh,x>2806:tb,thp}
            kch{m<923:tv,gb}
            hjb{a<3134:R,a<3605:A,x<3863:R,R}
            qqx{a<463:mzj,x<3421:bt,m<1948:fn,hm}
            tqq{x>2718:R,x<2531:R,R}
            grm{x<2602:jzs,m<1343:tfh,rxf}
            jd{x<3784:R,kqz}
            ffs{a<1606:R,x<2581:A,m<3572:R,A}
            rp{x<2972:A,R}
            xt{x>1600:zs,s<117:R,m>896:A,rc}
            sm{s<1090:A,x>2853:xqd,dcq}
            jv{x<2646:A,a>3211:A,a<2787:R,A}
            kmd{m>2964:A,m<2233:A,s<1396:A,A}
            shn{a<3180:A,a>3511:dgd,m<2680:R,A}
            tvg{a>588:A,a>302:A,x<2987:R,R}
            fbq{m>2979:xj,xp}
            qf{a<351:R,R}
            lcx{x<3134:A,a>3035:A,a<2960:R,R}
            fhg{m<1254:kn,m<1841:R,R}
            mzj{x<3496:R,A}
            qvl{a<1677:A,A}
            tvm{s>3088:npf,cz}
            khk{m<814:R,a>895:rh,a<553:A,zmq}
            txt{x<3633:qtx,ssv}
            rjt{m>1917:A,s>1599:A,R}
            kr{s<687:qt,dvx}
            nbh{s<1409:A,m>3191:A,m<2744:R,fth}
            ctd{s>1618:R,R}
            xh{m>1661:R,a<2986:A,a>3365:A,R}
            ntv{s>71:A,A}
            xnr{m<2882:lxx,s<1615:cmm,vfq}
            cx{x<2761:R,s>600:R,m>3047:A,R}
            bpm{a<2131:gvz,x>3626:A,x>3372:ffm,dx}
            gc{m>1612:A,R}
            hb{m<529:A,m>563:A,m<548:A,A}
            br{x>3204:A,A}
            tb{s<3595:nq,x>2903:zmh,s<3845:zp,bkp}
            hst{s<560:tjn,tnk}
            cf{s>1127:R,R}
            cr{s<1678:A,R}
            hgt{s<1486:A,R}
            mn{a<1199:A,m>300:A,R}
            flj{a>3113:R,A}
            zsq{a<358:R,R}
            kd{m>3591:sqb,a>1967:hg,x>3661:jgt,hxf}
            kh{x>1502:A,A}
            fr{a<2316:A,m>456:A,A}
            qnp{s<843:A,s>1041:A,a<190:vkk,R}
            vqr{s<1356:fgd,msq}
            qrb{s>869:lq,x<2777:qq,x>3358:mv,xxl}
            mlt{x>3226:A,a<3573:R,x>2685:R,A}
            ct{s>1471:tr,R}
            rn{s<1162:ttc,qsn}
            llh{s<622:R,R}
            hj{a<449:A,s<696:pmk,A}
            xzz{x<3309:dj,x>3672:jd,sqz}
            bxd{x>3026:qqx,m<1825:tc,m>3007:gs,xc}
            kv{a>1262:R,a<1254:A,s>3458:R,R}
            kfk{m>1254:A,m>773:A,A}
            pk{a<512:A,x>3494:R,a<614:R,A}
            nrq{m>3308:R,m<2741:R,s<835:A,A}
            gs{s>3383:R,x<2686:R,m>3551:qf,A}
            cd{m>1618:mft,vzt}
            xqd{a>2230:A,A}
            gth{m>591:jbx,x>3239:trj,sm}
            tnk{a<3178:lk,x<1503:R,m<2254:A,gkt}
            trj{x<3631:tq,m<489:kbk,s<1086:hb,hs}
            sp{x<3305:R,m>1214:A,ppq}
            lxx{s>1519:A,m<2862:R,s>1331:R,R}
            rq{x>1859:sfl,s>733:R,a<612:R,A}
            ffm{m<266:R,x>3472:R,R}
            nr{x>3510:R,a<129:R,x>3317:R,R}
            frd{a>1514:krn,a<681:lsl,qmc}
            pv{a>1102:A,s>1751:A,a>842:A,A}
            vl{m>2629:gj,x<3863:jrx,a>1621:xml,qc}
            tjn{s<241:R,xh}
            sdv{s>1286:ckq,s<1240:lnl,s>1264:jhs,ps}
            dgd{x<2871:A,R}
            tks{a<578:A,m<1066:R,x<2822:A,A}
            lk{s<897:A,x<1501:A,A}
            jnk{a>1238:kv,A}
            mxp{a>3199:R,m<1416:A,A}
            bxb{m<1615:A,m>1668:R,R}
            xdh{a>932:R,x<2938:R,s<1716:A,R}
            dn{m<1232:A,m>1548:bxb,x>501:R,qz}
            gkh{s>1362:R,x<2757:R,s<1326:A,R}
            lrs{x>848:R,m<3173:R,R}
            cdc{x>3607:R,s>3464:R,A}
            dg{s<2438:bfj,qkb}
            gkj{a<1585:A,s<2495:kh,R}
            jt{x>3682:A,a<1086:A,a>1111:R,A}
            jc{s<1669:A,a>2896:R,A}
            xnd{m<680:A,A}
            zm{m<1426:A,kgd}
            crf{m<3561:A,R}
            vm{x>2896:A,a>2021:R,A}
            bbn{s<420:qvm,A}
            lnn{a>1072:A,R}
            sbp{x<2643:cvb,A}
            gcn{x<1476:R,x>1712:R,A}
            tr{x>1458:R,x<1364:R,m>3349:R,A}
            ng{m>318:R,R}
            rb{x>1519:A,a>454:R,a>286:R,A}
            mb{x>3141:A,x>2778:A,A}
            jzs{a>2219:bbx,s>1664:msb,A}
            cvk{x<817:A,m<1143:R,m<1256:cnh,pfl}
            tq{x>3454:A,x<3370:A,A}
            rmt{s<2364:qbt,s>2753:R,s>2497:sl,rss}
            bpx{x<3627:R,a<3383:A,s>3383:A,A}
            fc{a>2944:A,R}
            mh{x<3348:nb,x<3768:A,a<628:ckl,msm}
            dp{x<3811:A,m<565:A,m<710:A,R}
            bhr{s<1345:R,m>3378:A,R}
            fff{m>1438:R,zrq}
            lx{s<3779:xn,s<3908:gc,s<3963:R,R}
            cb{m<2315:A,a<410:R,x>2786:R,R}
            crc{s<1486:R,a>3577:xhf,x<2854:flk,hk}
            sz{s>193:sdq,tqs}
            cn{s>1652:R,m>680:A,x>3227:A,R}
            gx{m>2758:R,a<453:R,s>1448:A,A}
            ptk{x<955:R,flj}
            jf{x>3214:lnn,a<1070:bjf,R}
            cng{m>2361:zc,dz}
            tzb{m>3402:A,s<2200:A,s>2294:R,R}
            lmf{a>2725:dqd,x<2909:A,m>2669:A,R}
            xcr{a>355:A,x>3857:R,a>175:R,R}
            thp{a<1214:R,s<3440:gm,kc}
            ckq{a<1918:bhr,a>2834:R,gkh}
            krn{s<223:R,R}
            sfl{s>703:A,s>602:R,A}
            lg{m>2037:lbd,m>790:dsz,s>776:psk,gdd}
            mff{s<996:R,A}
            kj{x>2627:R,m<197:R,mn}
            mvp{a<2378:A,m>547:R,R}
            xp{s>1561:R,s>1419:R,x<3720:kxq,R}
            msq{x<2879:A,a>2140:mnz,x<3062:R,R}
            rm{x>781:A,x<362:R,m>2098:A,A}
            kbp{m>2813:chx,s<1122:R,s<1150:pt,R}
            bjf{a<1054:A,x>3110:A,m<2481:A,R}
            dz{s<1524:cl,a>1879:vtj,khk}
            cl{x>1731:R,a<1673:kfk,m>1112:fhh,vhn}
            lhh{a>1444:R,a>1341:R,R}
            qfg{x>1080:hst,jl}
            gls{x<3041:cv,a>1282:mpx,bnl}
            sdq{s>348:A,R}
            gtg{s>1384:gx,s<1291:R,m<2683:fz,R}
            kc{a>1299:R,m>2622:A,x>2569:A,A}
            lrd{s>369:A,x<1336:A,s>153:R,R}
            rxf{a<2128:vk,x<2909:rjt,s>1589:R,A}
            hz{m<535:R,R}
            rdc{a>556:zg,a<264:tzb,m<3328:bg,R}
            flk{x>2495:A,A}
            njk{m>3043:A,x<1397:lrd,A}
            hx{s>2840:R,a>420:A,A}
            jrx{x<3810:lf,m<2571:A,R}
            px{m>2939:A,x>3564:rxv,x<3310:R,A}
            zrq{m>721:A,s<3368:R,x<3672:A,A}
            xhg{s<919:nrq,m>2801:R,m>2373:R,hbd}
            qqj{m<2840:xdh,vnt}
            lt{x<2990:A,R}
            gf{x>964:R,x>871:R,s<1402:R,A}
            qn{s<304:tdq,fbd}
            lqk{x<3713:rv,vl}
            xml{x<3920:vlg,m>2525:dmb,A}
            pn{x>2710:R,A}
            zs{x<1908:A,R}
            tqs{x>3091:A,s>85:R,s<47:A,A}
            fqn{x<2834:ncm,x<2965:vm,A}
            ckx{s<3576:R,x<3506:A,A}
            ffk{a>2178:R,R}
            dl{s>310:A,x>2451:gvf,bv}
            xmr{m<3262:A,a<2826:tqq,R}
            xhf{m>2777:A,s<1532:A,s<1544:R,R}
            vk{a>1985:R,a<1907:A,R}
            xl{x<498:A,R}
            kp{s>2682:A,xnt}
            ls{m<1801:lkf,R}
            dx{s<988:R,A}
            mft{a<1508:bbn,s>697:lc,x<1556:njk,lv}
            bm{m<2386:A,x<2668:A,A}
            zk{a>3483:A,m<1145:R,R}
            ps{a<1820:A,gpz}
            fmb{x<1634:tk,tbf}
            fdp{m>3329:R,a<1800:A,A}
            jx{a<2408:vnf,dzz}
            pmd{m>1378:A,s>1559:A,R}
            qfp{s>504:R,R}
            dcq{x>2591:A,x<2408:A,x<2515:A,R}
            lq{x>2830:mrv,hz}
            cnh{a<3251:A,m<1200:R,m>1228:R,A}
            ttc{a>1971:qfg,x<1229:kr,a<1159:jb,cd}
            tzc{x<1402:A,m>3113:rb,s<736:A,A}
            xj{s<1501:R,A}
            qq{m>523:A,bsp}
            rf{m>529:A,R}
            gz{x<3158:zk,a<3623:R,m<1141:hzc,ckx}
            jbp{m>1897:R,A}
            mgp{s>1009:R,m<667:R,x>3319:A,R}
            sc{s>593:xl,x<592:A,m<1490:scm,ss}
            gq{x<3191:R,R}
            pmk{m<1438:R,A}
            rvf{m<2716:R,s<1288:A,R}
            kx{s<884:R,a<2109:R,x<2629:A,A}
            jdb{x<1670:tzc,tz}
            lml{m<649:A,m<895:R,A}
            kbk{x<3824:R,x>3928:R,s>1078:A,A}
            fs{m>1048:xf,m>484:bnb,sb}
            lbd{s>762:zq,m<3172:nkr,qn}
            zmh{m<2572:R,s>3859:R,A}
            hn{s>3814:A,s<3624:clt,nx}
            cjm{s>1418:xzz,x<3332:sdv,kd}
            nhl{s<1550:jbp,a>511:gq,R}
            dmz{x>2828:A,x<2593:A,x<2730:dm,R}
            dfl{s>966:R,a<274:R,R}
            jgd{a>1743:dkk,s<3155:dg,a>1026:gls,qfh}
            tl{a<2495:R,x>511:R,R}
            fg{m>1284:mlt,A}
            bv{s>106:R,m<313:R,a<2281:R,R}
            gdd{s>489:mz,x>3299:jk,x<2674:pjv,frd}
            vnf{s>1491:qqj,a>918:sbp,gtg}
            dkk{m>2099:ld,a<3012:qx,kb}
            qtx{x<3517:A,a>2166:R,R}
            kqz{m<3493:A,A}
            ppq{m<1118:R,x<3570:R,A}
            ql{m>768:R,A}
            scq{x>3341:kbq,a>2656:vb,s<1466:vqr,grm}
            sqb{m<3846:clf,m>3932:R,m<3890:R,xm}
            dqd{m<2815:A,x>2785:A,R}
            jbx{s>1090:cpf,a<2048:mgp,bft}
            dsq{s<615:R,m>2311:R,s>830:A,R}
            bvb{s>1371:A,x<905:A,a<1732:R,A}
            lkf{m<752:A,a<1247:R,A}
            gm{s<3333:R,A}
            vzt{s>579:fmb,x>1817:rg,ljl}
            mrv{x<3473:R,A}
            nk{m<2055:A,a<957:R,bh}
            tvk{x<786:A,A}
            qnm{m>3551:A,s>1207:R,s<1200:R,A}
            qlc{s<620:A,A}
            psn{a>536:zlt,m<737:qnp,nqm}
            jk{s<164:ntv,a>1424:bp,a>799:nfc,qvg}
            xrc{s>2829:A,s>2221:R,A}
            cvb{s<1361:A,s>1414:A,A}
            vvh{a<2160:ls,m<2518:jsc,nbh}
            hvg{a>2032:A,m<3401:R,R}
            tpd{s>1708:mx,m<1878:ctd,hr}
            cjz{s<1430:ntr,R}
            snz{s>270:kch,xt}
            vzh{m<1010:hd,xbq}
            bxh{s<3239:R,x>437:A,m>3240:fdp,qd}
            bbx{m<962:A,A}
            qvm{m<2516:A,s>225:A,s<94:R,R}
            ljt{x<1597:R,a<2078:A,x<1915:R,R}
            qc{m>2526:R,hgt}
                        
            {x=590,m=690,a=867,s=1366}
            {x=1880,m=905,a=1184,s=14}
            {x=1820,m=1050,a=3128,s=1788}
            {x=1748,m=1470,a=1044,s=369}
            {x=2329,m=734,a=365,s=72}
            {x=157,m=2096,a=648,s=1980}
            {x=495,m=2177,a=1513,s=1183}
            {x=465,m=867,a=1934,s=537}
            {x=1372,m=1490,a=77,s=128}
            {x=1609,m=2,a=1417,s=2835}
            {x=863,m=192,a=101,s=412}
            {x=424,m=2193,a=243,s=557}
            {x=1120,m=2201,a=1403,s=1949}
            {x=13,m=52,a=1670,s=1246}
            {x=199,m=74,a=1349,s=381}
            {x=1410,m=3632,a=1535,s=1375}
            {x=1738,m=1405,a=1355,s=1826}
            {x=2398,m=1112,a=2480,s=541}
            {x=710,m=22,a=1045,s=275}
            {x=2767,m=46,a=10,s=2185}
            {x=1597,m=483,a=1963,s=265}
            {x=1113,m=2298,a=49,s=2190}
            {x=937,m=661,a=1091,s=136}
            {x=505,m=1271,a=25,s=1976}
            {x=1289,m=53,a=243,s=845}
            {x=2102,m=451,a=829,s=492}
            {x=1344,m=1127,a=155,s=79}
            {x=154,m=228,a=197,s=2154}
            {x=1603,m=38,a=137,s=1241}
            {x=97,m=1834,a=1616,s=1090}
            {x=1197,m=786,a=562,s=2339}
            {x=632,m=316,a=1687,s=1}
            {x=1525,m=3801,a=1119,s=2118}
            {x=723,m=1418,a=228,s=442}
            {x=3620,m=213,a=1699,s=1187}
            {x=3012,m=2652,a=525,s=1692}
            {x=652,m=693,a=2661,s=886}
            {x=1277,m=1170,a=305,s=76}
            {x=1796,m=64,a=398,s=483}
            {x=214,m=56,a=3443,s=506}
            {x=515,m=505,a=2339,s=995}
            {x=2493,m=346,a=154,s=1427}
            {x=552,m=727,a=384,s=228}
            {x=22,m=687,a=2100,s=127}
            {x=271,m=753,a=3147,s=1606}
            {x=1777,m=258,a=898,s=1026}
            {x=1852,m=3218,a=1616,s=445}
            {x=1051,m=1062,a=1678,s=1003}
            {x=580,m=819,a=1312,s=237}
            {x=33,m=8,a=17,s=995}
            {x=162,m=5,a=1776,s=1488}
            {x=278,m=682,a=1600,s=55}
            {x=749,m=2075,a=2018,s=271}
            {x=1985,m=56,a=875,s=1896}
            {x=767,m=3409,a=266,s=319}
            {x=53,m=51,a=33,s=688}
            {x=83,m=2098,a=1222,s=2934}
            {x=349,m=937,a=574,s=857}
            {x=213,m=2313,a=126,s=1002}
            {x=4,m=454,a=171,s=123}
            {x=742,m=105,a=70,s=1987}
            {x=101,m=1759,a=998,s=485}
            {x=1792,m=3499,a=458,s=1944}
            {x=632,m=277,a=1322,s=2210}
            {x=119,m=58,a=1222,s=482}
            {x=2638,m=577,a=232,s=1210}
            {x=329,m=105,a=171,s=1069}
            {x=96,m=130,a=1156,s=1635}
            {x=405,m=715,a=2084,s=266}
            {x=180,m=2972,a=101,s=351}
            {x=1902,m=129,a=17,s=2197}
            {x=2546,m=481,a=50,s=522}
            {x=62,m=1925,a=1755,s=75}
            {x=1382,m=869,a=106,s=1045}
            {x=142,m=30,a=744,s=541}
            {x=1907,m=125,a=1093,s=22}
            {x=1520,m=136,a=973,s=2074}
            {x=975,m=733,a=1537,s=714}
            {x=2842,m=416,a=370,s=203}
            {x=776,m=156,a=230,s=205}
            {x=846,m=541,a=1154,s=863}
            {x=2457,m=1055,a=2220,s=321}
            {x=811,m=217,a=861,s=1414}
            {x=83,m=144,a=459,s=199}
            {x=134,m=2646,a=1064,s=65}
            {x=3421,m=1490,a=1848,s=1469}
            {x=312,m=1357,a=795,s=949}
            {x=535,m=2409,a=372,s=1036}
            {x=1224,m=97,a=1633,s=218}
            {x=1995,m=235,a=1772,s=1497}
            {x=706,m=426,a=3709,s=864}
            {x=1180,m=1977,a=1265,s=3481}
            {x=1647,m=282,a=2460,s=3450}
            {x=376,m=365,a=245,s=2137}
            {x=505,m=346,a=1175,s=720}
            {x=3242,m=2413,a=53,s=742}
            {x=668,m=2080,a=476,s=1493}
            {x=70,m=2465,a=29,s=18}
            {x=27,m=1464,a=539,s=877}
            {x=54,m=2616,a=338,s=2877}
            {x=1650,m=976,a=1159,s=245}
            {x=1899,m=1899,a=1781,s=146}
            {x=148,m=212,a=277,s=81}
            {x=855,m=1285,a=307,s=1417}
            {x=70,m=1859,a=286,s=2422}
            {x=865,m=1888,a=365,s=3056}
            {x=2109,m=1575,a=528,s=1120}
            {x=211,m=1024,a=652,s=277}
            {x=107,m=1612,a=1009,s=2401}
            {x=93,m=1363,a=1874,s=557}
            {x=268,m=315,a=47,s=551}
            {x=1164,m=647,a=859,s=242}
            {x=506,m=1819,a=235,s=847}
            {x=2218,m=246,a=1033,s=615}
            {x=114,m=899,a=2141,s=924}
            {x=22,m=57,a=1374,s=777}
            {x=41,m=185,a=330,s=2054}
            {x=2625,m=40,a=142,s=92}
            {x=931,m=3360,a=132,s=666}
            {x=1337,m=474,a=142,s=20}
            {x=636,m=312,a=10,s=614}
            {x=199,m=581,a=40,s=2489}
            {x=1559,m=216,a=172,s=1906}
            {x=2067,m=1492,a=155,s=2588}
            {x=195,m=1888,a=521,s=422}
            {x=1461,m=141,a=742,s=1863}
            {x=196,m=800,a=2861,s=857}
            {x=1194,m=432,a=884,s=2243}
            {x=33,m=1682,a=1323,s=1062}
            {x=22,m=329,a=153,s=2}
            {x=1640,m=2487,a=1028,s=585}
            {x=268,m=109,a=190,s=1268}
            {x=532,m=454,a=1826,s=2236}
            {x=339,m=701,a=84,s=2957}
            {x=1329,m=7,a=1793,s=1012}
            {x=1652,m=1027,a=2568,s=1534}
            {x=60,m=222,a=1504,s=684}
            {x=1703,m=808,a=2407,s=1060}
            {x=752,m=704,a=8,s=330}
            {x=80,m=1754,a=2389,s=1269}
            {x=2073,m=1261,a=2276,s=274}
            {x=1756,m=3001,a=244,s=1335}
            {x=1117,m=146,a=356,s=258}
            {x=63,m=1592,a=1010,s=369}
            {x=2746,m=2084,a=333,s=1191}
            {x=988,m=462,a=1070,s=494}
            {x=2797,m=516,a=1717,s=142}
            {x=445,m=636,a=2390,s=2192}
            {x=466,m=1574,a=1570,s=578}
            {x=1000,m=561,a=2455,s=2285}
            {x=1008,m=931,a=2436,s=2644}
            {x=152,m=852,a=120,s=837}
            {x=144,m=389,a=1124,s=153}
            {x=1913,m=385,a=2029,s=2555}
            {x=2237,m=167,a=274,s=660}
            {x=3027,m=112,a=970,s=1278}
            {x=24,m=1169,a=776,s=32}
            {x=3070,m=552,a=1829,s=1649}
            {x=1336,m=2711,a=209,s=528}
            {x=2425,m=602,a=940,s=167}
            {x=396,m=1645,a=1119,s=1253}
            {x=2950,m=56,a=232,s=1676}
            {x=433,m=1731,a=3,s=1338}
            {x=1146,m=62,a=1780,s=1728}
            {x=917,m=1000,a=178,s=2560}
            {x=57,m=549,a=41,s=1256}
            {x=1017,m=274,a=379,s=181}
            {x=1206,m=1616,a=249,s=3739}
            {x=733,m=1923,a=13,s=34}
            {x=2713,m=627,a=28,s=376}
            {x=1441,m=1320,a=162,s=834}
            {x=1328,m=674,a=1631,s=2590}
            {x=606,m=559,a=17,s=999}
            {x=119,m=862,a=1080,s=405}
            {x=2077,m=1009,a=91,s=1116}
            {x=283,m=159,a=1888,s=41}
            {x=1010,m=603,a=807,s=138}
            {x=1635,m=1650,a=150,s=1240}
            {x=318,m=710,a=1487,s=1838}
            {x=256,m=16,a=485,s=1262}
            {x=771,m=2044,a=1191,s=362}
            {x=566,m=1659,a=2143,s=806}
            {x=483,m=1108,a=1440,s=1638}
            {x=599,m=473,a=110,s=99}
            {x=1175,m=1448,a=3208,s=347}
            {x=1097,m=742,a=2558,s=314}
            {x=40,m=2481,a=276,s=1912}
            {x=91,m=167,a=532,s=145}
            {x=1260,m=2944,a=523,s=982}
            {x=1694,m=1855,a=619,s=195}
            {x=1407,m=2927,a=2235,s=1367}
            {x=529,m=69,a=189,s=86}
            {x=1236,m=726,a=1136,s=89}
            {x=1898,m=677,a=867,s=783}
            {x=440,m=704,a=1588,s=13}
            {x=771,m=2713,a=708,s=286}
            {x=388,m=46,a=1598,s=1254}
            {x=1097,m=2288,a=2526,s=2364}
            {x=2362,m=200,a=165,s=774}
            {x=735,m=285,a=1358,s=16}""";
}
