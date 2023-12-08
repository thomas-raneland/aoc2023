import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day08 {
    public static void main(String[] args) {
        partI(TESTINPUT);
        partI(INPUT);

        partII(TESTINPUT);
        partII(TESTINPUT2);
        partII(INPUT);
    }

    private static void partI(String input) {
        var instructions = parseInstructions(input);
        var network = parseNetwork(input);
        var repetitions = repetitions(instructions, network, "AAA");
        System.out.println(repetitions * instructions.size());
    }

    private static void partII(String input) {
        var instructions = parseInstructions(input);
        var network = parseNetwork(input);
        var nodes = network.left.keySet().stream().filter(node -> node.endsWith("A"));
        var repetitions = nodes.mapToLong(node -> repetitions(instructions, network, node)).reduce((a, b) -> a * b).orElseThrow();
        System.out.println(repetitions * instructions.size());
    }

    private enum Instruction {L, R}

    private static List<Instruction> parseInstructions(String input) {
        var line = input.lines().findFirst().orElseThrow();
        return line.chars().mapToObj(c -> c == 'L' ? Instruction.L : Instruction.R).toList();
    }

    private record Network(Map<String, String> left, Map<String, String> right) {}

    private static Network parseNetwork(String input) {
        var left = input.lines().skip(2).collect(Collectors.toMap(l -> l.substring(0, 3), l -> l.substring(7, 10)));
        var right = input.lines().skip(2).collect(Collectors.toMap(l -> l.substring(0, 3), l -> l.substring(12, 15)));
        return new Network(left, right);
    }

    private static long repetitions(List<Instruction> instructions, Network network, String node) {
        var repetitions = 0;

        while (!node.endsWith("Z")) {
            for (Instruction instruction : instructions) {
                node = (instruction == Instruction.L) ? network.left().get(node) : network.right().get(node);
            }

            repetitions++;
        }

        return repetitions;
    }

    private static final String TESTINPUT = """
            LLR
                        
            AAA = (BBB, BBB)
            BBB = (AAA, ZZZ)
            ZZZ = (ZZZ, ZZZ)""";

    private static final String TESTINPUT2 = """
            LR
                        
            11A = (11B, XXX)
            11B = (XXX, 11Z)
            11Z = (11B, XXX)
            22A = (22B, XXX)
            22B = (22C, 22C)
            22C = (22Z, 22Z)
            22Z = (22B, 22B)
            XXX = (XXX, XXX)""";

    private static final String INPUT = """
            LRRLLRLLRRLRRLLRRLLRLRRRLLRRLRRRLRRLRRRLLRRLLRLLRRLRLRRRLRRLLRRRLRLRRLRRLRLRLRLLRLRRRLLRLLRRLRRRLRLRLRRRLRRLLRRRLRLRRLRRLLRRLRRRLRRLRRLRRLLRLRLRRLLRLLRRRLRRLRRLRRRLRLLRRRLRRRLRRLLRRRLRRRLRLLRLRRLRLLRRLLLRRLRRLRRLRLRRRLRRLLRLRRRLRRLRLLLRRLRRLRRRLLLRLLLLRRLRLLLRLRRRLRRRLRLRRRLLLLRLRRRLRLLLRRLRLRRLRRLRRRLRRRR
                        
            LHF = (QTF, KKT)
            VRQ = (MDV, RCX)
            NDF = (CHQ, FJM)
            XXG = (NJP, GPX)
            GDG = (JCJ, BDD)
            PHF = (RMF, MHK)
            LQX = (NMG, RSJ)
            RPG = (VRH, BVV)
            GFT = (MJK, KKN)
            HHK = (GVN, XJH)
            CQF = (BRS, VCC)
            QJS = (XLL, VTX)
            XGL = (PKM, CND)
            CHZ = (JLQ, KPQ)
            FGP = (HNL, NBQ)
            LND = (SGB, DXR)
            TXQ = (RKL, LDV)
            JBC = (DRP, KFT)
            QSL = (KFS, LDC)
            SXJ = (JFQ, JFQ)
            TDN = (XDB, DSC)
            CFF = (BBB, BBB)
            HTS = (FHM, XLD)
            KCF = (RMN, XXG)
            MNG = (HCB, RMQ)
            BMX = (LVJ, MNF)
            NXC = (HKD, DDM)
            JCV = (KDL, KBR)
            SLM = (PGL, VCG)
            CDB = (GBQ, BQC)
            CJH = (TXM, QCK)
            RQH = (LDB, DRM)
            MMV = (XSP, FGD)
            DKM = (BXT, LXG)
            LLJ = (RXS, VRQ)
            BXN = (KFX, JCL)
            RNC = (XKR, RPG)
            BVV = (PFG, TFP)
            MXL = (KGT, KGT)
            HKV = (SMM, GFM)
            XTC = (BPV, RND)
            GBQ = (JPD, JQL)
            KTL = (BVL, BFH)
            XFF = (CJQ, DKH)
            KGH = (PSF, RVB)
            GBF = (HLR, HKV)
            DXK = (KGS, JFJ)
            JMJ = (FNB, XCC)
            KXD = (VXB, TRL)
            LJQ = (JBC, JRV)
            VFK = (HMK, KQB)
            DXM = (GBF, VDV)
            JTN = (TCS, HGP)
            RHH = (RXC, LGB)
            MXR = (PBC, XQN)
            GVK = (MSQ, SHN)
            RBX = (XTX, GNJ)
            MVX = (PFQ, DVH)
            VLK = (QBQ, DLN)
            SBQ = (MMQ, FTP)
            JMG = (MJV, MJV)
            BVB = (SJB, KBQ)
            LTF = (THJ, TVN)
            GGX = (PVB, TCP)
            XLS = (PGL, VCG)
            MMP = (LDB, DRM)
            NVD = (HKF, CKX)
            GDT = (TJT, KNK)
            GNJ = (HTS, KDH)
            MJK = (QJF, PQQ)
            XDL = (NXC, MTB)
            QDK = (HBD, NRK)
            CRQ = (NDF, HQQ)
            GBN = (GMC, QQB)
            FJM = (SVF, KQV)
            DTF = (XKR, RPG)
            CRG = (JNN, PKP)
            BKL = (GCL, CHZ)
            LDF = (CTN, VDH)
            MMQ = (MJC, MMV)
            TMR = (KQM, MNL)
            QXL = (XLS, SLM)
            PBC = (DVJ, XLX)
            XQZ = (TRL, VXB)
            KHG = (JPJ, TRX)
            DVH = (HMB, VJB)
            CBN = (CQF, VTK)
            QBC = (CVJ, PHF)
            XSV = (QTF, KKT)
            RDV = (RXQ, NHJ)
            DVT = (JPJ, TRX)
            VRX = (FPX, LTF)
            TLX = (FRC, XFH)
            HTK = (TXJ, KQH)
            KGT = (CFK, CFK)
            NBS = (VKK, HTQ)
            BBV = (FVQ, GPF)
            FNP = (MXQ, MHX)
            LQN = (BKB, GKN)
            FHJ = (TXM, QCK)
            PKL = (PFL, TKG)
            FXX = (NXL, HJB)
            QPG = (BLM, RRH)
            QTD = (VBR, HQT)
            VKK = (PMX, XDJ)
            BQH = (DDL, NXT)
            KBQ = (SHV, JGB)
            BND = (RGX, KQJ)
            DCL = (QXL, LRV)
            TCS = (FKJ, NQN)
            TCN = (BXH, QHS)
            HKD = (HPD, CTP)
            BKR = (GTT, GTT)
            GFV = (FLC, CVX)
            DFQ = (BXK, CDB)
            CNJ = (GCX, XXJ)
            CST = (PLK, GMF)
            BXA = (VXB, TRL)
            CND = (GFT, QMC)
            XDB = (XTC, JVF)
            HJN = (VMT, MCP)
            RSJ = (TCB, DCL)
            QHS = (SSN, QBF)
            JVV = (DXR, SGB)
            FHG = (FLK, CQB)
            HSR = (BDP, GCD)
            KTQ = (LXG, BXT)
            QVT = (QSQ, BQH)
            HPP = (XKQ, HSR)
            NHB = (BVH, CBN)
            PKG = (RCK, CHT)
            KKN = (QJF, PQQ)
            VCV = (JQQ, RSX)
            HXC = (RQH, MMP)
            MCP = (MMD, PFN)
            LQH = (PHK, JFB)
            PFF = (CLF, BLQ)
            QSQ = (NXT, DDL)
            NRJ = (HCB, RMQ)
            JQQ = (QJR, PMK)
            PHK = (PLP, LCQ)
            JDK = (GTM, SHR)
            JFJ = (VFK, HSM)
            TDD = (PBS, DBG)
            JHM = (TSR, BKL)
            RSX = (PMK, QJR)
            TJF = (BMX, VLS)
            HLR = (GFM, SMM)
            QRJ = (RBH, QPL)
            GGP = (TLQ, FJD)
            KNV = (KGX, MLN)
            XQN = (DVJ, XLX)
            VDV = (HKV, HLR)
            QTF = (TRR, NQS)
            VHL = (TDN, LHG)
            XHJ = (PLM, TVB)
            RJN = (XXJ, GCX)
            BTQ = (BFH, BVL)
            GDF = (GGP, RJD)
            MTB = (HKD, DDM)
            TPJ = (FHJ, CJH)
            TSR = (GCL, GCL)
            QPQ = (CQB, FLK)
            GVS = (GBC, SPX)
            MGF = (XPN, GGX)
            GTT = (GQX, GNQ)
            FFJ = (RLM, FTQ)
            BXH = (SSN, QBF)
            JQN = (CHT, RCK)
            JMH = (PNS, DGS)
            QTB = (SHM, LRP)
            NTJ = (SJN, BQB)
            GCD = (TDB, HPS)
            LSX = (XMQ, QTD)
            HJQ = (VGX, NBH)
            GFM = (LNT, CBD)
            PFN = (KGH, CSM)
            QCJ = (CVX, FLC)
            SHN = (CBG, XHJ)
            BSX = (CDQ, MBF)
            CXV = (MTB, NXC)
            SJB = (JGB, SHV)
            VRH = (TFP, PFG)
            HMB = (KTL, BTQ)
            DDM = (CTP, HPD)
            DCT = (HNJ, PMT)
            NXF = (NBX, XDC)
            BTF = (BVB, TBX)
            CQX = (KGX, MLN)
            MLN = (HRK, HKN)
            KBA = (XPQ, PSS)
            NXT = (PJH, VVM)
            RXQ = (DST, CRG)
            RDG = (BKB, GKN)
            LTR = (PPN, JTN)
            DXJ = (BKR, DBT)
            PLM = (DNJ, QTB)
            RGS = (VHL, HMF)
            SGS = (CQX, KNV)
            JRG = (NBQ, HNL)
            BKB = (FHN, XFF)
            RJD = (FJD, TLQ)
            FXG = (LCN, MHV)
            RMN = (GPX, NJP)
            VCG = (BDV, DNF)
            TXX = (BQH, QSQ)
            PHL = (XPN, GGX)
            XCC = (FGP, JRG)
            RXF = (XXR, NLS)
            BRS = (MGF, PHL)
            NLS = (LQH, JMR)
            GTM = (NJS, HCT)
            GDJ = (XCC, FNB)
            QBQ = (DQH, QJS)
            TRL = (MFK, TDM)
            FTP = (MMV, MJC)
            TXT = (NNC, NHR)
            SNN = (XJR, KMG)
            PMN = (KTQ, DKM)
            HQQ = (FJM, CHQ)
            LNT = (PBP, PMN)
            PGN = (XCR, NBR)
            NJS = (CNJ, RJN)
            BDM = (SGS, VMF)
            MJS = (LQX, RHT)
            NBL = (MXL, MXL)
            LCQ = (TFM, GVK)
            MLL = (CND, PKM)
            JKJ = (TBX, BVB)
            DGS = (QBC, GKJ)
            BFH = (CPX, GMD)
            JSX = (JBC, JRV)
            NKK = (RPV, SNG)
            BJN = (SXJ, RKH)
            DKH = (HLH, RBX)
            FJD = (FPL, LSP)
            JLQ = (TXQ, KKV)
            KNK = (GVS, LRX)
            RXC = (NMF, MKK)
            TGP = (QPG, VXM)
            CPX = (HFM, KCF)
            QCG = (XFH, FRC)
            LGB = (MKK, NMF)
            GMP = (QVT, TXX)
            RHM = (RCR, NGF)
            DNJ = (SHM, LRP)
            SXB = (XJJ, HGX)
            TVB = (QTB, DNJ)
            MHV = (MNG, NRJ)
            RPJ = (MSF, JRC)
            HQT = (TRD, KDG)
            TBP = (HCG, KMZ)
            BNK = (JCL, KFX)
            CDQ = (HHR, VCB)
            BLM = (CFF, SQK)
            FHP = (RGS, TLV)
            FNB = (FGP, JRG)
            GPF = (FTL, HJQ)
            DRF = (FTQ, RLM)
            GFL = (LND, JVV)
            HMK = (QCG, TLX)
            HSM = (HMK, KQB)
            XXR = (LQH, JMR)
            PKT = (NPS, QXV)
            BKJ = (KHC, KMJ)
            BVL = (GMD, CPX)
            MMR = (RJD, GGP)
            VXM = (BLM, RRH)
            JDM = (XKS, HPP)
            RKL = (PKG, JQN)
            GCL = (KPQ, JLQ)
            NHR = (FXX, GCH)
            SMR = (LTD, NVD)
            RKH = (JFQ, DXJ)
            SHR = (NJS, HCT)
            CVJ = (MHK, RMF)
            JRV = (DRP, KFT)
            PBS = (QDK, HNP)
            SGB = (QRN, MBP)
            FVD = (QTD, XMQ)
            TDP = (DHR, LGQ)
            CQB = (RHH, MLC)
            JRC = (NKK, QQM)
            RMF = (MHR, HHK)
            QXJ = (SML, TMR)
            HMF = (LHG, TDN)
            TVG = (DPN, MKT)
            RRL = (RCR, NGF)
            JLJ = (XKS, HPP)
            KKT = (NQS, TRR)
            KBM = (JMJ, GDJ)
            TCP = (QNK, LDF)
            XMQ = (VBR, HQT)
            MKT = (PDG, SMR)
            XGQ = (KDL, KBR)
            JFV = (FFB, TKX)
            NBX = (QSL, RFG)
            DPN = (PDG, SMR)
            LRP = (LDM, HXC)
            PSS = (RGM, VKL)
            KFX = (VRX, HDM)
            FRC = (TCN, XSK)
            RND = (JCQ, PNL)
            MLC = (RXC, LGB)
            PPN = (TCS, HGP)
            PVT = (SXJ, RKH)
            HDT = (MLL, XGL)
            KRV = (BVH, CBN)
            JGB = (GFV, QCJ)
            TKX = (KBM, FLS)
            LMP = (DBG, PBS)
            DDL = (VVM, PJH)
            RGX = (HBG, BPQ)
            MHX = (DFQ, VPN)
            NGT = (MJS, FGN)
            VVG = (LGQ, DHR)
            KDG = (SSD, MXR)
            ZZZ = (GNQ, GQX)
            JPJ = (TVG, PPV)
            MBF = (HHR, VCB)
            FQL = (KGS, JFJ)
            GDK = (KGT, MLF)
            HLH = (GNJ, XTX)
            HNL = (JQD, LGK)
            MNL = (MLV, DBK)
            KHC = (BDM, SSB)
            JJL = (QTH, VHG)
            QDX = (RXS, VRQ)
            PMX = (DMK, FNP)
            VTA = (MJS, FGN)
            GNQ = (KJJ, CLM)
            MNC = (BLQ, CLF)
            HNJ = (KKS, VDQ)
            LVJ = (JCV, XGQ)
            JQD = (CDX, GVC)
            DLN = (QJS, DQH)
            KNN = (HDT, QKH)
            JJR = (JDM, JLJ)
            KDH = (FHM, XLD)
            NRK = (MXB, JJR)
            NXP = (DGS, PNS)
            DRP = (MXN, KLT)
            QTH = (GJX, CRQ)
            QXV = (JMH, NXP)
            RFG = (LDC, KFS)
            HJB = (JSX, LJQ)
            LSZ = (QPQ, FHG)
            CGJ = (RGX, KQJ)
            XCR = (FMQ, JHK)
            CJR = (XDL, CXV)
            NHJ = (CRG, DST)
            JVR = (PLK, GMF)
            JCJ = (JKP, JKP)
            VLS = (LVJ, MNF)
            TRX = (TVG, PPV)
            JNN = (NBL, BTM)
            KQH = (RHB, KTR)
            KQM = (DBK, MLV)
            SSN = (GMP, QPC)
            TFP = (XSV, LHF)
            SHM = (HXC, LDM)
            XJJ = (NTJ, FBG)
            QCK = (VDR, QXJ)
            PGL = (DNF, BDV)
            XFH = (XSK, TCN)
            VGJ = (SHR, GTM)
            VXB = (MFK, TDM)
            RQD = (VLS, BMX)
            RCX = (PVT, BJN)
            SJN = (FFJ, DRF)
            AAA = (GQX, GNQ)
            FPL = (FDS, VDF)
            VTK = (VCC, BRS)
            HGX = (FBG, NTJ)
            PLK = (HCN, NXF)
            FHN = (CJQ, DKH)
            VGX = (LPH, FHP)
            XSK = (BXH, QHS)
            BSV = (GPF, FVQ)
            QNG = (VHQ, VHQ)
            KQB = (TLX, QCG)
            PPV = (DPN, MKT)
            SML = (KQM, MNL)
            FKJ = (QQX, BGF)
            QKH = (MLL, XGL)
            JKP = (NMB, NMB)
            NQS = (GHQ, GSQ)
            GCH = (HJB, NXL)
            XSP = (CGJ, BND)
            HBD = (JJR, MXB)
            DPB = (BKJ, TNX)
            JMR = (JFB, PHK)
            KTR = (BBV, BSV)
            CDK = (XDL, CXV)
            HVG = (MNC, PFF)
            XPJ = (VHQ, TBP)
            GXL = (DDR, DCT)
            GHQ = (BNK, BXN)
            JQL = (LMP, TDD)
            VVM = (VCV, DFP)
            NPR = (VLK, JGS)
            PMT = (KKS, VDQ)
            GBR = (VHG, QTH)
            TRR = (GHQ, GSQ)
            XJR = (DXM, SCF)
            BGF = (TPJ, MKG)
            VDH = (SXB, HQK)
            GJX = (HQQ, NDF)
            CBD = (PBP, PMN)
            VMF = (CQX, KNV)
            HNP = (HBD, NRK)
            DSC = (XTC, JVF)
            HCB = (PKL, FGM)
            BTM = (MXL, GDK)
            DBT = (GTT, ZZZ)
            FGN = (RHT, LQX)
            FSP = (KXD, KXD)
            JFQ = (BKR, BKR)
            NJN = (RPJ, MLK)
            TVN = (HPV, MKX)
            XKS = (XKQ, HSR)
            PDG = (NVD, LTD)
            XLL = (CVT, GDG)
            GVN = (SNN, FQD)
            CDX = (VVG, TDP)
            TMS = (JMG, CQP)
            QNK = (VDH, CTN)
            MSQ = (XHJ, CBG)
            JHK = (HQV, NJN)
            MKX = (FVD, LSX)
            KQJ = (HBG, BPQ)
            NMF = (DNR, PKT)
            VCB = (KDB, RDV)
            MRM = (SHB, QPN)
            HVK = (HTQ, VKK)
            KMS = (HFV, GXL)
            MNF = (XGQ, JCV)
            HPV = (FVD, LSX)
            LXG = (GFL, CRT)
            TFM = (SHN, MSQ)
            PKM = (QMC, GFT)
            KFT = (KLT, MXN)
            RHT = (NMG, RSJ)
            HRK = (LPP, DGB)
            BXT = (GFL, CRT)
            XLX = (JVR, CST)
            GQX = (KJJ, CLM)
            TLQ = (FPL, LSP)
            LHG = (XDB, DSC)
            GKJ = (CVJ, PHF)
            CRT = (JVV, LND)
            KDN = (VLK, JGS)
            QNN = (PFQ, DVH)
            RXS = (MDV, RCX)
            JCQ = (JDK, VGJ)
            HTQ = (XDJ, PMX)
            CHQ = (SVF, KQV)
            KFS = (DRS, HGD)
            GQP = (SHB, QPN)
            MJV = (TSR, TSR)
            KPQ = (KKV, TXQ)
            RBH = (NHB, KRV)
            SNG = (GDT, BTV)
            CFK = (FHG, QPQ)
            RPV = (BTV, GDT)
            MMD = (CSM, KGH)
            PJH = (DFP, VCV)
            LGK = (CDX, GVC)
            DBK = (CJR, CDK)
            LCN = (MNG, NRJ)
            RVB = (GBR, JJL)
            TCB = (LRV, QXL)
            XTX = (HTS, KDH)
            VDN = (BKJ, TNX)
            CLF = (LQN, RDG)
            CJQ = (RBX, HLH)
            JPD = (LMP, TDD)
            MGV = (PPN, JTN)
            RSH = (TGP, LST)
            BPV = (PNL, JCQ)
            SRN = (NLS, XXR)
            FTQ = (FXG, DMP)
            FVQ = (FTL, HJQ)
            GBC = (KDN, NPR)
            VTX = (CVT, GDG)
            KGS = (VFK, HSM)
            MHK = (HHK, MHR)
            KLT = (QRJ, XCQ)
            LPP = (DXK, FQL)
            DNF = (KLS, TXT)
            HMA = (KPQ, JLQ)
            DVR = (HVK, NBS)
            NPS = (JMH, NXP)
            PVB = (LDF, QNK)
            DMK = (MHX, MXQ)
            CTP = (XNV, KMS)
            KGX = (HRK, HKN)
            RMQ = (FGM, PKL)
            QQX = (MKG, TPJ)
            DPD = (QSM, GXS)
            GXS = (FSD, KNN)
            RGM = (VDN, DPB)
            HPD = (KMS, XNV)
            HBG = (RXF, SRN)
            GKN = (FHN, XFF)
            PNS = (QBC, GKJ)
            FDS = (RQD, TJF)
            QPN = (BTF, JKJ)
            DQH = (VTX, XLL)
            XCQ = (RBH, QPL)
            TJT = (LRX, GVS)
            MVT = (NGT, KRZ)
            HKF = (JFV, JJN)
            XJH = (FQD, SNN)
            RCK = (RRL, RHM)
            MSF = (QQM, NKK)
            CLM = (SNM, FHB)
            VHG = (CRQ, GJX)
            QBF = (GMP, QPC)
            SVF = (DGD, TMS)
            NBH = (LPH, FHP)
            JCL = (HDM, VRX)
            VDR = (SML, TMR)
            SMM = (LNT, CBD)
            BPQ = (SRN, RXF)
            VTN = (FSP, DTG)
            PNL = (VGJ, JDK)
            BTV = (TJT, KNK)
            MLF = (CFK, LSZ)
            XKR = (BVV, VRH)
            BMJ = (HVK, NBS)
            PLP = (TFM, GVK)
            KDL = (XJL, THC)
            JVF = (BPV, RND)
            FLK = (RHH, MLC)
            DGB = (FQL, DXK)
            DBG = (QDK, HNP)
            LDC = (HGD, DRS)
            DGD = (JMG, CQP)
            LPH = (TLV, RGS)
            DTG = (KXD, XQZ)
            GVC = (TDP, VVG)
            TLV = (HMF, VHL)
            NMG = (TCB, DCL)
            XDC = (RFG, QSL)
            MFK = (LTR, MGV)
            SHB = (BTF, JKJ)
            TDM = (LTR, MGV)
            RCR = (THB, KBX)
            KDB = (RXQ, NHJ)
            PFG = (LHF, XSV)
            HDM = (FPX, LTF)
            SSB = (VMF, SGS)
            FHM = (GBN, VRK)
            SQK = (BBB, VTN)
            BVH = (VTK, CQF)
            TXM = (VDR, QXJ)
            TDB = (DVT, KHG)
            XXJ = (DPD, FXN)
            VKL = (DPB, VDN)
            NNC = (FXX, GCH)
            XLD = (VRK, GBN)
            FGM = (PFL, TKG)
            MJC = (FGD, XSP)
            HPS = (KHG, DVT)
            DHR = (QNN, MVX)
            VHQ = (HCG, HCG)
            MLV = (CJR, CDK)
            LTD = (CKX, HKF)
            QPL = (KRV, NHB)
            MDV = (PVT, BJN)
            MXB = (JLJ, JDM)
            KMZ = (PSS, XPQ)
            QPC = (QVT, TXX)
            BBB = (FSP, FSP)
            NQN = (QQX, BGF)
            VDQ = (MMR, GDF)
            HQK = (XJJ, HGX)
            VRK = (GMC, QQB)
            HCT = (CNJ, RJN)
            VDF = (TJF, RQD)
            XKQ = (GCD, BDP)
            XJL = (RSH, DSD)
            BLQ = (LQN, RDG)
            QRN = (DTF, RNC)
            KKS = (GDF, MMR)
            HKN = (DGB, LPP)
            SCF = (GBF, VDV)
            VPN = (BXK, CDB)
            BDV = (KLS, TXT)
            TBX = (SJB, KBQ)
            MLK = (JRC, MSF)
            VJB = (BTQ, KTL)
            XNV = (GXL, HFV)
            QQM = (SNG, RPV)
            VMT = (MMD, PFN)
            MXQ = (VPN, DFQ)
            TXJ = (KTR, RHB)
            LSP = (VDF, FDS)
            KKV = (LDV, RKL)
            KMG = (SCF, DXM)
            MBP = (DTF, RNC)
            TRD = (SSD, MXR)
            CTT = (DVR, BMJ)
            PMK = (MRM, GQP)
            NMB = (NGT, NGT)
            HQV = (MLK, RPJ)
            THJ = (HPV, MKX)
            NXL = (JSX, LJQ)
            XPQ = (VKL, RGM)
            CTN = (SXB, HQK)
            DDR = (PMT, HNJ)
            BQB = (FFJ, DRF)
            SHV = (QCJ, GFV)
            KLS = (NNC, NHR)
            LST = (VXM, QPG)
            VCC = (PHL, MGF)
            FQD = (XJR, KMG)
            KJJ = (FHB, SNM)
            LDM = (RQH, MMP)
            GMD = (KCF, HFM)
            CKX = (JJN, JFV)
            NBQ = (JQD, LGK)
            QJR = (MRM, GQP)
            HRF = (TXJ, KQH)
            FLC = (HVG, QGM)
            DRS = (BSX, XQD)
            DRM = (QNG, XPJ)
            PFL = (PGN, XCF)
            MKG = (FHJ, CJH)
            NBR = (JHK, FMQ)
            PFQ = (HMB, VJB)
            CQP = (MJV, JHM)
            HHR = (KDB, RDV)
            TKG = (XCF, PGN)
            KMJ = (SSB, BDM)
            HGP = (NQN, FKJ)
            QSM = (KNN, FSD)
            QGM = (MNC, PFF)
            LNK = (NMB, MVT)
            DNR = (QXV, NPS)
            RRH = (CFF, SQK)
            VBR = (TRD, KDG)
            CHT = (RHM, RRL)
            HFV = (DDR, DCT)
            XQD = (MBF, CDQ)
            HGD = (BSX, XQD)
            FTL = (VGX, NBH)
            SSD = (XQN, PBC)
            FSD = (HDT, QKH)
            PSF = (JJL, GBR)
            CSM = (RVB, PSF)
            PQQ = (TRN, HJN)
            BXK = (BQC, GBQ)
            QJF = (HJN, TRN)
            NJP = (LLJ, QDX)
            DSD = (LST, TGP)
            BDD = (JKP, LNK)
            SPX = (NPR, KDN)
            PFP = (BMJ, DVR)
            KRZ = (FGN, MJS)
            LGQ = (QNN, MVX)
            DMP = (MHV, LCN)
            DVJ = (JVR, CST)
            CVX = (HVG, QGM)
            CVT = (JCJ, BDD)
            LRX = (SPX, GBC)
            TNX = (KMJ, KHC)
            DST = (JNN, PKP)
            JFB = (LCQ, PLP)
            GPX = (QDX, LLJ)
            CBG = (TVB, PLM)
            BDP = (HPS, TDB)
            JJN = (TKX, FFB)
            HCN = (XDC, NBX)
            XDJ = (FNP, DMK)
            LDV = (PKG, JQN)
            XCF = (XCR, NBR)
            MXN = (QRJ, XCQ)
            XPN = (PVB, TCP)
            MKK = (PKT, DNR)
            PBP = (DKM, KTQ)
            NGF = (KBX, THB)
            RLM = (DMP, FXG)
            FFB = (FLS, KBM)
            FLS = (JMJ, GDJ)
            QMC = (MJK, KKN)
            QQB = (LQQ, SBQ)
            HFM = (RMN, XXG)
            BQC = (JQL, JPD)
            GMF = (HCN, NXF)
            FMQ = (HQV, NJN)
            KBR = (THC, XJL)
            DXR = (QRN, MBP)
            FGD = (BND, CGJ)
            THC = (RSH, DSD)
            LRV = (SLM, XLS)
            JGS = (QBQ, DLN)
            GMC = (LQQ, SBQ)
            LDB = (QNG, QNG)
            LQQ = (MMQ, FTP)
            GSQ = (BXN, BNK)
            MHR = (GVN, XJH)
            THB = (HTK, HRF)
            SNM = (PFP, CTT)
            FBG = (BQB, SJN)
            KBX = (HRF, HTK)
            TRN = (MCP, VMT)
            KQV = (DGD, TMS)
            HCG = (XPQ, PSS)
            PKP = (NBL, BTM)
            FPX = (TVN, THJ)
            GCX = (DPD, FXN)
            FHB = (PFP, CTT)
            FXN = (QSM, GXS)
            DFP = (JQQ, RSX)
            HLA = (FHG, QPQ)
            RHB = (BBV, BSV)""";
}
