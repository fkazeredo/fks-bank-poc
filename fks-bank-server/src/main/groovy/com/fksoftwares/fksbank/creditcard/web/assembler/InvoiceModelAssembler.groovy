package com.fksoftwares.fksbank.creditcard.web.assembler

import com.fksoftwares.fksbank.creditcard.Invoice
import com.fksoftwares.fksbank.creditcard.web.model.InvoiceModel
import org.modelmapper.ModelMapper

class InvoiceModelAssembler {

    private static ModelMapper modelMapper = new ModelMapper()

    static InvoiceModel toModel(Invoice invoice) {
        return modelMapper.map(invoice, InvoiceModel)
    }

}
