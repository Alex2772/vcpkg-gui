package ru.alex2772.vcpkggui.model;

import javax.swing.table.AbstractTableModel;
import java.util.Comparator;
import java.util.List;

public class PackageTableModel extends AbstractTableModel {
    private final List<VcpkgPackage> mPackages;

    public PackageTableModel(List<VcpkgPackage> packages) {
        mPackages = packages;
    }

    @Override
    public int getRowCount() {
        return mPackages.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }


    public VcpkgPackage getPackage(int index) {
        return mPackages.get(index);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        VcpkgPackage item = mPackages.get(rowIndex);
        switch (columnIndex) {
            case 0: return item.getName();
            case 1: return item.getVersion();
            case 2: return item.getPlatform();
        }
        return null;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0: return "Package";
            case 1: return "Version";
            case 2: return "Platform";
        }
        return super.getColumnName(column);
    }
}
