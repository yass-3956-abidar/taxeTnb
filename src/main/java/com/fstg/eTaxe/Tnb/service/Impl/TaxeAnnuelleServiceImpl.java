/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fstg.eTaxe.Tnb.service.Impl;

import com.fstg.eTaxe.Tnb.bean.Proprietaire;
import com.fstg.eTaxe.Tnb.bean.TauxTaxe;
import com.fstg.eTaxe.Tnb.bean.TaxeAnnuelle;
import com.fstg.eTaxe.Tnb.bean.Terrain;
import com.fstg.eTaxe.Tnb.dao.TauxTaxeDao;
import com.fstg.eTaxe.Tnb.dao.TaxeAnnuelleDao;
import com.fstg.eTaxe.Tnb.service.CategorieService;
import com.fstg.eTaxe.Tnb.service.TauxTaxeRetardService;
import com.fstg.eTaxe.Tnb.service.TaxeAnnuelleService;
import com.fstg.eTaxe.Tnb.service.TerrainService;
import com.fstg.eTaxe.Tnb.service.util.DateUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javassist.expr.Cast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author alikhyatti
 */
@Service
public class TaxeAnnuelleServiceImpl implements TaxeAnnuelleService {

    @Autowired
    private TaxeAnnuelleDao taxeAnnuelleDao;

    @Autowired
    private TerrainService terrainService;

    @Autowired
    private TauxTaxeDao tauxTaxeDao;

    @Autowired
    private TauxTaxeRetardService TauxTaxeRetardService;

    @Override
    public TaxeAnnuelle findByAnneeAndTerrainAndProprietaire(int annee, Terrain Terrain, Proprietaire proprietaire) {
        return taxeAnnuelleDao.findByAnneeAndTerrainAndProprietaire(annee, Terrain, proprietaire);
    }

    //
    @Override
    public void save(TaxeAnnuelle taxeAnnuelle) {
        Date dateNow = new Date();
        taxeAnnuelle.setTauxTaxe(tauxTaxeDao.findByCategorieAndDateNow(terrainService.findById(taxeAnnuelle.getTerrain().getId()).getCategorie(), DateUtil.parse(DateUtil.format(dateNow))));
        taxeAnnuelle.setTauxTaxeRetard(TauxTaxeRetardService.findByCategorie(terrainService.findById(taxeAnnuelle.getTerrain().getId()).getCategorie()));
        taxeAnnuelle.setDatePaiement(dateNow);
        //        taxeAnnuelleDao.save(calculeMontant2(taxeAnnuelle));
        taxeAnnuelle.setMontant(terrainService.calculeMontantTotal(taxeAnnuelle.getTerrain().getId(), taxeAnnuelle.getAnnee()));
        taxeAnnuelleDao.save(taxeAnnuelle);
    }
//    @Override
//    public void save(TaxeAnnuelle taxeAnnuelle) {
//        Date dateNow = new Date();
//        taxeAnnuelle.setTauxTaxe(tauxTaxeDao.findByCategorieAndDateNow(terrainService.findById(taxeAnnuelle.getTerrain().getId()).getCategorie(), DateUtil.parseYearIntegerToDate(taxeAnnuelle.getAnnee())));
//        taxeAnnuelle.setTauxTaxeRetard(TauxTaxeRetardService.findByCategorie(terrainService.findById(taxeAnnuelle.getTerrain().getId()).getCategorie()));
//        taxeAnnuelle.setDatePaiement(dateNow);
//        taxeAnnuelleDao.save(calculeMontant2(taxeAnnuelle));
//
//    }

    @Override
    public List<TaxeAnnuelle> findAll() {
        return taxeAnnuelleDao.findAll();
    }

    //For Save
    @Override
    public TaxeAnnuelle calculeMontant2(TaxeAnnuelle taxeAnnuelle) {
        BigDecimal montant = new BigDecimal(BigInteger.ZERO);
        montant = (terrainService.findById(taxeAnnuelle.getTerrain().getId()).getSurface()).multiply(tauxTaxeDao.findById(taxeAnnuelle.getTauxTaxe().getId()).get().getMontantTaxe());
        taxeAnnuelle.setMontant(montant);
        return taxeAnnuelle;
    }

    //calculeMontant
    @Override
    public void calculeMontantRetard(Long id) {
        TaxeAnnuelle taxeAnnuelle = new TaxeAnnuelle();
        taxeAnnuelle = findById(id);
        BigDecimal montant = new BigDecimal(BigInteger.ZERO);
        BigDecimal divisor = new BigDecimal(BigInteger.TEN.multiply(BigInteger.TEN));
        Date dateNow = new Date();

        long nombreMois = DateUtil.periodMonth(DateUtil.parseYearIntegerToDate(taxeAnnuelle.getAnnee()), dateNow);

//        String dateNowStr = DateUtil.format(dateNow);
//        String dateStr = String.valueOf(taxeAnnuelle.getAnnee()) + "-01-01";
//        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // format jour / mois / année
//        LocalDate date1 = LocalDate.parse(dateNowStr, format);
//        LocalDate date2 = LocalDate.parse(dateStr, format);
//        Period period = Period.between(date2, date1);
//        nombreMois = period.getYears() * 12 + period.getMonths();
        if (nombreMois < taxeAnnuelle.getTauxTaxeRetard().getNombreMois()) {
            montant = calculeMontant2(taxeAnnuelle).getMontant();
            taxeAnnuelle.setMontant(montant);
        } else if (nombreMois > taxeAnnuelle.getTauxTaxeRetard().getNombreMois()) {
            montant = calculeMontant2(taxeAnnuelle).getMontant().multiply(taxeAnnuelle.getTauxTaxeRetard().getTauxTaxeRetard().divide(divisor)).multiply(BigDecimal.valueOf(nombreMois - 3)).add(calculeMontant2(taxeAnnuelle).getMontant());
            taxeAnnuelle.setMontant(montant);
        }

        save(taxeAnnuelle);
    }
//
//    public BigDecimal calculeMontantRetard2(Long id) {
//        Terrain terrain = new Terrain();
//        terrain = findById(id);
//        BigDecimal montant = new BigDecimal(BigInteger.ZERO);
//        BigDecimal divisor = new BigDecimal(BigInteger.TEN.multiply(BigInteger.TEN));
//        Date dateNow = new Date();
//        long nombreMois = DateUtil.periodMonth(DateUtil.parseYearIntegerToDate(taxeAnnuelle.getAnnee()), dateNow);
//
//        if (nombreMois > taxeAnnuelle.getTauxTaxeRetard().getNombreMois()) {
//            montant = calculeMontant2(taxeAnnuelle).getMontant().multiply(taxeAnnuelle.getTauxTaxeRetard().getTauxTaxeRetard().divide(divisor)).multiply(BigDecimal.valueOf(nombreMois - 3)).add(calculeMontant2(taxeAnnuelle).getMontant());
//            return montant;
//        } else {
//            return montant;
//        }
//    }
//
//    public BigDecimal calculeMontantAnnuelle(Long id) {
//        BigDecimal montant = new BigDecimal(BigInteger.ZERO);
//        TaxeAnnuelle taxeAnnuelle = new TaxeAnnuelle();
//        taxeAnnuelle = findById(id);
//        montant = (terrainService.findById(taxeAnnuelle.getTerrain().getId()).getSurface()).multiply(tauxTaxeDao.findById(taxeAnnuelle.getTauxTaxe().getId()).get().getMontantTaxe());
//        taxeAnnuelle.setMontant(montant);
//        return montant;
//    }
//
//    public BigDecimal calculeMontantTotal(Long id) {
//        BigDecimal montant = calculeMontantRetard2(id).add(calculeMontantAnnuelle(id));
//        return montant;
//    }

//    //there is a NullPointerException here !!!!!!!!!!!!!!
//    //For Update 
//    @Override
//    public TaxeAnnuelle calculeMontant(TaxeAnnuelle taxeAnnuelle) {
//        BigDecimal montant = new BigDecimal(BigInteger.ZERO);
//        montant = (taxeAnnuelle.getTerrain().getSurface()).multiply(taxeAnnuelle.getTauxTaxe().getMontantTaxe());
//        taxeAnnuelle.setMontant(montant);
//        return taxeAnnuelle;
//    }
    // not implemented yet
//    @Override
//    public void update(TaxeAnnuelle taxeAnnuelle) {
//         taxeAnnuelleDao.save(taxeAnnuelle);
//    }
    // not implemented yet
//    @Override
//    public List<Integer> anneestaxesNonPayeeByReferanceTerrain(String referance) {
//        return null;
//    }
    @Override
    public TaxeAnnuelle findById(Long id) {
        return taxeAnnuelleDao.findById(id).get();
    }

    //khdama ^_^
    @Override
    public void update(Long id) {
        TaxeAnnuelle taxeAnnuelle = new TaxeAnnuelle();
        taxeAnnuelle = findById(id);
//        if (taxeAnnuelle.getTauxTaxe() != null) {
//            TauxTaxe tauxTaxe = tauxTaxeDao.findById(taxeAnnuelle.getTauxTaxe().getId()).get();
//            taxeAnnuelle2.setTauxTaxe(tauxTaxe);
//        }
        Date dateNow = new Date();
        taxeAnnuelle.setTauxTaxe(tauxTaxeDao.findByCategorieAndDateNow(terrainService.findById(taxeAnnuelle.getTerrain().getId()).getCategorie(), DateUtil.parse(DateUtil.format(dateNow))));
        taxeAnnuelle.setTauxTaxeRetard(TauxTaxeRetardService.findByCategorie(taxeAnnuelle.getTerrain().getCategorie()));
        save(taxeAnnuelle);
    }

    @Override
    public TaxeAnnuelle findByAnneeAndTerrain(int annee, Terrain Terrain) {
        return taxeAnnuelleDao.findByAnneeAndTerrain(annee, Terrain);
    }
}
